import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.*
import com.drew.metadata.exif.GpsDescriptor
import com.drew.metadata.exif.GpsDirectory
import groovy.json.JsonSlurper
class GetGeolocation {
    static boolean isNegative = false
    String[] getGeoData(String filePath) {
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
            println("The Latitude is : " + latitude)
            println("The Longitude is : " + longitude)
            return geo_code
        }
        catch (NullPointerException e)
        {
            println('No Geodata found inside the file')
            e.printStackTrace()
        }
    }

    String[] filterCoordinates(String geoPosition) {
        String[] to_return = new String[2]
        char[] geoPos = geoPosition.toCharArray()
        for (i in 0..<geoPos.length) {
            char ch = geoPosition.charAt(i)
            int intChar = (int) ch
            if (geoPos[0] == '-'){
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

    double toNumerics(String integerPart, String decimalPart) {
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
        if (isNegative){
            return (t*-1-min)
        }
        else{
            return t + min
        }
    }

    String getLocationName(double latitude, double longitude)throws FileNotFoundException {
        //make connection and accept the response in text form
        def tokenCode = 'pk.17e12176634dca0bb1707cc3d0d7d929'
        def uri = 'https://us1.locationiq.com/v1/reverse.php?key=' + tokenCode + '&lat=' + latitude + '&lon=' + longitude + '=&format=json'
        def url = new URL(uri)
        def response = new URL(uri).text
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection()
        if (httpURLConnection.responseCode != 200) {
            System.exit(0)
            return 'Location cannot be defined'
        }
        else {
            println(response)
            def jsonSlurper = new JsonSlurper()
            //use jSonSlurper to convert the text into pretty json
            Object ob = jsonSlurper.parseText(response)
            def linkedHashMap = [:]
            def address = ''
            ob.each {
                linkedHashMap << it
                address = linkedHashMap.get('display_name')
            }
            return address
        }
    }
}