import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.*
import com.drew.metadata.exif.GpsDescriptor
import com.drew.metadata.exif.GpsDirectory

class GetMetadata {
    static boolean isNegative = false
    static void main(String[] args) {
        try{
            String filePath = 'D:\\neg988021.jpg'
            InputStream imageInputStream = new FileInputStream(filePath)
            String[] geo_code = new String[2]
            geo_code = getGeoData(filePath) //reading the geodata from the Exif
            ReverseGeolocatingAPI reverseGeocodeAPICall = new ReverseGeolocatingAPI()
            String[] latitude = filterCoordinates(geo_code[0])
            String[] longitude = filterCoordinates(geo_code[1])
            double filteredLatitude = toNumerics(latitude[0], latitude[1])
            double filteredLongitude = toNumerics(longitude[0], longitude[1])
            String result = reverseGeocodeAPICall.getLocationName(filteredLatitude, filteredLongitude)
            println(result)
        }
        catch (FileNotFoundException e){
            println("File Not Found")
            e.printStackTrace()
        }
        catch (NullPointerException e){
            println('No GPS Information in the File')
            e.printStackTrace()
        }
    }

    static String[] getGeoData(String filePath) {
        String[] geo_code = new String[2]
        InputStream imageInputStream = new FileInputStream(filePath)
        Metadata metadata = ImageMetadataReader.readMetadata(imageInputStream)
        try {
            GpsDirectory directory = (GpsDirectory) metadata.getFirstDirectoryOfType(GpsDirectory.class)
            GpsDescriptor gpsDescriptor = new GpsDescriptor(directory)
            def latitude = gpsDescriptor.getGpsLatitudeDescription()
            def longitude = gpsDescriptor.getGpsLongitudeDescription()
            //println(latitude.class.simpleName)
            geo_code[0] = latitude
            geo_code[1] = longitude
            println("Latitude : " + latitude)
            println("Longitude : " + longitude)
            return geo_code
        }
        catch (NullPointerException e)
        {
            println('No Geodata')
            e.printStackTrace()
        }
    }

    static String[] filterCoordinates(String geoPosition) {
        String[] to_return = new String[2]
        char[] geoPos = geoPosition.toCharArray()
        for (i in 0..<geoPos.length) {
            char ch = geoPosition.charAt(i)
            int intChar = (int) ch
            if (geoPos[0]=='-'){
                isNegative = true
                switch (intChar) {
                    case 176:
                    case 65533:
                        to_return[0] = geoPosition.substring(1, i)
                        to_return[1] = geoPosition.substring(i + 2)
                        break
                }
            }
            else {
                switch (intChar) {
                    case 176:
                    case 65533:
                        to_return[0] = geoPosition.substring(0, i)
                        to_return[1] = geoPosition.substring(i + 2)
                        break
                }
            }
        }
        return to_return
    }

    static double toNumerics(String integerPart, String decimalPart) {
        int t
        t = integerPart.toInteger()//convert Integer part which is String to Integer
        String minutes = '', seconds = '' //to store minutes and seconds of the coordinate
        for (int i = 0; i < decimalPart.length(); i++) {
            char ch = decimalPart.charAt(i)
            if (ch == '\'') {
                minutes = decimalPart.substring(0, i)
                seconds = decimalPart.substring(i + 2, decimalPart.length() - 1)
            }
        }
        double min = (minutes.toDouble() + seconds.toDouble()/60)/60 //converting minutes and seconds to degree
        //println(t+min)
        if (isNegative){
            return (t*-1-min)
        }

        else{
            return t + min
        }
    }
}