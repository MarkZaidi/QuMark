//Benchmark script
//Performs a set of commands reflective of what one would typically perform in QuPath.
//Here, we create annotations from a pixel classifier, detect cells, classify cells, and export cell measurements

//load libraries
import org.codehaus.groovy.control.messages.WarningMessage
import qupath.lib.gui.tools.MeasurementExporter
import qupath.lib.objects.PathCellObject
import groovy.time.*
import static qupath.lib.gui.scripting.QPEx.*

//Clear cache before running the benchmark
def store =  QuPathGUI.getInstance().getViewer().getImageRegionStore()
try {
    print "Clearing cache..."
    store.cache.clear()
    store.thumbnailCache.clear()
    System.gc()
} catch (Exception e2) {
    e2.printStackTrace()
}

def timeStart_total = new Date()
//set stain information
setImageType('BRIGHTFIELD_H_E');
setColorDeconvolutionStains('{"Name" : "H&E default", "Stain 1" : "Hematoxylin", "Values 1" : "0.65111 0.70119 0.29049 ", "Stain 2" : "Eosin", "Values 2" : "0.2159 0.8012 0.5581 ", "Background" : " 255 255 255 "}');

//Clear out any preexisting stuff
clearAllObjects();

//Create annotations from premade pixel classifier
print('Creating annotations from premade pixel classifier')
def timeStart_PixelClassifier = new Date()
createAnnotationsFromPixelClassifier("benchmark_classifier_v3", 1000.0, 1000.0, "INCLUDE_IGNORED", "SELECT_NEW")
//createAnnotationsFromPixelClassifier("benchmark_classifier_v3", 1000.0, 1000.0, "SPLIT", "INCLUDE_IGNORED", "SELECT_NEW")
TimeDuration PixelClassifier_duration = TimeCategory.minus(new Date(), timeStart_PixelClassifier)



//Detect cells
print('Detecting cells')
def timeStart_CellDetection = new Date()
selectAnnotations();
runPlugin('qupath.imagej.detect.cells.WatershedCellDetection', '{"detectionImageBrightfield": "Hematoxylin OD",  "requestedPixelSizeMicrons": 0.5,  "backgroundRadiusMicrons": 0.0,  "medianRadiusMicrons": 0.0,  "sigmaMicrons": 1.5,  "minAreaMicrons": 10.0,  "maxAreaMicrons": 400.0,  "threshold": 0.2,  "maxBackground": 2.0,  "watershedPostProcess": true,  "cellExpansionMicrons": 5.0,  "includeNuclei": true,  "smoothBoundaries": true,  "makeMeasurements": true}');
TimeDuration CellDetection_duration = TimeCategory.minus(new Date(), timeStart_CellDetection)

//Classify cells
print('Classifying cells')
def timeStart_CellClassification = new Date()
runObjectClassifier("Cytoplasmic_hematoxylin");
TimeDuration CellClassification_duration = TimeCategory.minus(new Date(), timeStart_CellClassification)


//Export measurements as csv file
print('Exporting measurements')
def timeStart_MeasurementExport = new Date()

// Get the list of all images in the current project
def project = getProject()
def imagesToExport = project.getImageList()
//Need to save results, else export will be blank
getProject().getEntry(getCurrentImageData()).saveImageData(getCurrentImageData())
// Separate each measurement value in the output file with a tab ("\t")
def separator = ","

// Choose the columns that will be included in the export
// Note: if 'columnsToInclude' is empty, all columns will be included
//def columnsToInclude = new String[]{"Name", "Class", "Nucleus: Area"}
def columnsToInclude = new String[]{}
// Choose the type of objects that the export will process
// Other possibilities include:
//    1. PathAnnotationObject
//    2. PathDetectionObject
//    3. PathRootObject
// Note: import statements should then be modified accordingly
def exportType = PathCellObject.class

// Choose your *full* output path
def outputPath = buildFilePath(PROJECT_BASE_DIR,"measurements.csv")
def outputFile = new File(outputPath)

// Create the measurementExporter and start the export
def exporter  = new MeasurementExporter()
                  .imageList(imagesToExport)            // Images from which measurements will be exported
                  .separator(separator)                 // Character that separates values
                  .includeOnlyColumns(columnsToInclude) // Columns are case-sensitive
                  .exportType(exportType)               // Type of objects to export
                  .exportMeasurements(outputFile)        // Start the export process

print "Done!"
TimeDuration MeasurementExport_duration = TimeCategory.minus(new Date(), timeStart_MeasurementExport)

//Calculate, consolidate, and write timings to .txt file in project base directory

TimeDuration total_duration = TimeCategory.minus(new Date(), timeStart_total)
println total_duration
//check that total number of detections meets the expected amount

detections = getDetectionObjects()
//Should be a predetermined constant for the number of cells that should be detected in the benchmark
expected_detections=355504

File timings =new File(buildFilePath(PROJECT_BASE_DIR,"timings_"+timeStart_total.getTime()+".txt"))

timings.append("QuMark version 2021_07_14" + "\n \n")
timings.append("Pixel Classifier: " + PixelClassifier_duration + "\n")
timings.append("Cell Detection: " + CellDetection_duration + "\n")
if (detections.size() != expected_detections) {
    print("WARNING: Number of detections(" + detections.size() +")does not meet expected count(" + expected_detections + "). Tiles may have failed during cell detection\n")
    timings.append('Cell Count Verification: FAIL\n')
}else{
    timings.append('Cell Count Verification: PASS\n')
}

timings.append("Cell Classification: " + CellClassification_duration + "\n")
timings.append("Measurement Export: " + MeasurementExport_duration + "\n \n")
timings.append("Overall time: " + total_duration + "\n")
timings.append(getQuPath().getBuildString() + "\n")
//Play a sound to alert user that benchmark is done
java.awt.Toolkit.defaultToolkit.beep()