# QuMark
A standardized benchmark for evaluating QuPath 0.2.3 performance
http://openslide.cs.cmu.edu/download/openslide-testdata/Aperio/CMU-1.svs

## Usage
- Download the QuPath project and groovy script present in this repository
- If not already installed, install QuPath https://qupath.readthedocs.io/en/stable/docs/intro/installation.html
- Download CMU-1.tif from http://openslide.cs.cmu.edu/download/openslide-testdata/Aperio/CMU-1.svs. Make sure you store the image on a fast drive such as an SSD
- Launch QuPath and open the project. It'll prompt you to specify where the image is located. Set the directory to the folder containing CMU-1.tif
- Do NOT open the project entry, as this may influence benchmark times. Launch the script editor, open the benchmark script, select "Run for project", include CMU-1.tif, and select "OK
- Benchmarking can take anywhere from 5 minutes to 2 hours, depending on how fast of a computer you have. Personally, it takes me ~5:45 to run the script
- In the project's base directory, a .txt file will be generated with the timings of each test. Please share this information with me, as well as your system configuration (processor, RAM, storage models, and total RAM available to QuPath)
- Feel free to play around with other parameters of your system configuration to see if it influences the benchmark times (reduce RAM available, enable XMP RAM overclock on BIOS, move image to HDD, etc.)
