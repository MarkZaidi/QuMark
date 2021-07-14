# QuMark
## Introduction
A standardized benchmark for evaluating QuPath 0.2.3 performance (*Qu*Path Bench*Mark*). There hasn't been a benchmarking platform for evaluating how well a system is configured for running QuPath. Furthermore, there are a variety of processes that differ in terms of processor thread utilization, meaning that more CPU cores won't always yield a better performance. As such, benchmarking systems would be a easy and reliable way to identify the ideal system for high throughput image analysis in QuPath.
![image](https://user-images.githubusercontent.com/52012166/124979425-08abd480-e001-11eb-9439-184f6574cb47.png)

Currently, the pixel classifier uses the most memory and 100% utilization for the majority of its time. Near the end, it swaps to a single thread process, which hops between two logical processors (red and blue lines).

The first part of the cell detection algorithm also uses a single thread, whereas the second half (when it starts printing into the log) is multithreadded and uses 100% utilization. If I had to guess, the first half splits the image up into tiles (single thread), whereas the second half performs the segmentation. But certainly, the first half of the cell detection algorithm would be heavily rate limiting on systems with a low single thread performance (i.e. servers).

The time spent on a single measurement cell classifier is negligible, as is measurement exporting. In the screenshot above and video below, I was running an older version that wasn't properly generating measurements. With that fixed in this current version, only a few seconds are added in processing time.
## Usage
- Download the QuPath project and groovy script present in this repository
- If not already installed, install QuPath 0.2.3 https://qupath.readthedocs.io/en/stable/docs/intro/installation.html
- Download CMU-1.tif from https://drive.google.com/file/d/15zgeD9_liFZrJ_HSgWxfoPiuPqm9vJ7d/view?usp=sharing. Make sure you store the image on a fast drive such as an SSD.
- Launch QuPath and open the project. It'll prompt you to specify where the image is located. Set the directory to the folder containing CMU-1.tif
- Do NOT open the project entry, as this may influence benchmark times. Launch the script editor, open the benchmark script, select "Run for project", include CMU-1.tif, and select "OK"
- Benchmarking can take anywhere from 5 minutes to 2 hours, depending on how fast of a computer you have. Personally, it takes me ~5:45 to run the script
- In the project's base directory, a .txt file will be generated with the timings of each test. Please share this information with me, as well as your system configuration (processor, RAM, storage models, and total RAM available to QuPath)
- Feel free to play around with other parameters of your system configuration to see if it influences the benchmark times (reduce RAM available, enable XMP RAM overclock on BIOS, move image to HDD, etc.)

Video demonstration of running the benchmark: https://www.youtube.com/watch?v=66GeU6u5Kko
## To do
- See if there’s a way to get system specifications exported through the benchmarking script
