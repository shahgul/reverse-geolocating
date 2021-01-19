class DriverClass {
    static void main(String[] args) {
        FileGrabber fileGrabber = new FileGrabber()
        fileGrabber.mainMethodJavaFX()
        def filePath = fileGrabber.test()
        println(filePath)
        GetGeolocation getGeolocation = new GetGeolocation(filePath)
        def geo_code = getGeolocation.getGeoData() //reading the geodata from the Exif
        String[] latitude = getGeolocation.filterCoordinates(geo_code[0])
        String[] longitude = getGeolocation.filterCoordinates(geo_code[1])
        double filteredLatitude = getGeolocation.toNumerics(latitude[0], latitude[1])
        double filteredLongitude = getGeolocation.toNumerics(longitude[0], longitude[1])
        println('Filtered Coordinates are ' + filteredLatitude +' '+ filteredLongitude)
        String result = getGeolocation.getLocationName(filteredLatitude, filteredLongitude)
        println(result)
    }
}