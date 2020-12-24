import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.*
import com.drew.metadata.exif.GpsDescriptor
import com.drew.metadata.exif.GpsDirectory
import groovy.json.JsonSlurper

class GetGeolocation {
    static String filePath
    GetGeolocation(String filePath){
        this.filePath = filePath
    }

    static boolean isNegative = false
    static String[] getGeoData() {
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
            println(e)
            return System.exit(0)
        }
    }

    static String[] filterCoordinates(String geoPosition) {
        String[] to_return = new String[2]
        char[] geoPos = geoPosition.toCharArray()
        for (i in 0..<geoPos.length) {
            char ch = geoPosition.charAt(i)
            int intChar = (int) ch
            if (geoPos[0] == '-' as char){
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
            if (ch == '\'' as char) {
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

    static String getLocationName(double latitude, double longitude) {
        //make connection and accept the response in text form
        def tokenCode = 'pk.17e12176634dca0bb1707cc3d0d7d929'
        def uri = 'https://us1.locationiq.com/v1/reverse.php?key=' + tokenCode + '&lat=' + latitude + '&lon=' + longitude + '=&format=json'
        def url = new URL(uri)
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection()
        int responseCode = httpURLConnection.getResponseCode()
        def responseAddress = ''
        if (responseCode != HttpURLConnection.HTTP_OK) {
            responseAddress = responseCode + ': Address Not Found\nThere is no Human Settlement found, not that we could find on the Settlement DataBase'
            return responseAddress
        }
        else {
            def responseText = new URL(uri).text
            def jsonSlurper = new JsonSlurper() //use jSonSlurper to convert the text into pretty json
            Object ob = jsonSlurper.parseText(responseText)
            def linkedHashMap = [:]
            ob.each {
                linkedHashMap << it
                responseAddress = linkedHashMap.get('display_name')
            }
            responseAddress = 'The address of the Photo is ' + responseAddress
            return responseAddress
        }
    }
}