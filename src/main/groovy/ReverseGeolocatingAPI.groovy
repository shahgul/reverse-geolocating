import groovy.json.JsonSlurper

class ReverseGeolocatingAPI {
        static String getLocationName(double latitude, double longitude)throws FileNotFoundException {
                //make connection and accept the response in text form
                def uri = 'https://us1.locationiq.com/v1/reverse.php?key=pk.fe91c18b19f2f67de8b8b136a81bfd8f&lat=' + latitude + '&lon=' + longitude + '=&format=json'
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