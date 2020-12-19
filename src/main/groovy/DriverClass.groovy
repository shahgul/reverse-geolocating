class DriverClass {
    static void main(String[] args) {
        GetGeolocation getGeolocation = new GetGeolocation()
        try{
            def filePath = 'D:\\gps988021.jpg'
            def geo_code = new String[2]
            geo_code = getGeolocation.getGeoData(filePath) //reading the geodata from the Exif
            String[] latitude = getGeolocation.filterCoordinates(geo_code[0])
            String[] longitude = getGeolocation.filterCoordinates(geo_code[1])
            double filteredLatitude = getGeolocation.toNumerics(latitude[0], latitude[1])
            double filteredLongitude = getGeolocation.toNumerics(longitude[0], longitude[1])
            String result = getGeolocation.getLocationName(filteredLatitude, filteredLongitude)
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
}