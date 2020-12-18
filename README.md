# reverse-geolocating

This personal project reads the geo-data from metadata using drewnoakes/metadata-extractor library and LocationIQ API.
As of 18th December, 2020 the limitation is following:
1) If the location is not found then the program breaks and shows FileNotFoundException(still needs catching).
2) If there is no geo-data in the metadata the program will just give No geo code error.

Future plan is to use deep learning(maybe) to analyse the image and tell the content of image.
