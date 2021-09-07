import tl = require('azure-pipelines-task-lib/task')
import fs = require('fs')
import path = require('path')
import pako = require('pako')

var locationMappings: Map<string, string> = new Map()
var thumbLocations: Array<string> = new Array()
var fileCounter: number = 0

async function uploadJGivenReport() {
    let jgivenReportLocation: string = getReportLocation()
    if (!fs.existsSync(jgivenReportLocation)) {
        tl.setResult(tl.TaskResult.Failed, "The given location does not exist.")
        return;
    }

    const dataPath: string = path.join(jgivenReportLocation, 'data')
    const attachmentsPath: string = path.join(dataPath, 'attachments')

    fs.mkdir(path.join(dataPath, 'parsed'), function (error) {
        if (error) {
            console.log(error)
            return
        }
    })

    uploadAllTheAttachments(attachmentsPath)
    uploadAllTheThumbnails()
    uploadTheLocationMappings(path.join(dataPath, 'mappings.js'))
    extractDataAndUpload(dataPath)
    addAttachmentWithNameAndType(path.join(jgivenReportLocation, 'app.bundle.js'), 'bundle')
    addAttachmentWithNameAndType(path.join(jgivenReportLocation, 'index.html'), 'html')
}

function uploadAllTheThumbnails() {
    for (let thumbnailLocation of thumbLocations) {
        let imageExtension: string = thumbnailLocation.slice(thumbnailLocation.lastIndexOf('.'))
        let imageLocation: string = thumbnailLocation.slice(0, thumbnailLocation.lastIndexOf('-thumb')) +
            thumbnailLocation.slice(thumbnailLocation.lastIndexOf('-thumb') + 6)
        let croppedLocation: string = imageLocation.slice(imageLocation.indexOf("attachments/"), imageLocation.length)

        locationMappings.forEach((value, key) => {
            if (value === croppedLocation) {
                let fileNameOfImage: string = key.slice(key.lastIndexOf(`jgivenFile_`), key.lastIndexOf(`.`))
                console.log(`##vso[task.addattachment type=attachmentFile;name=${fileNameOfImage}-thumb${imageExtension};]${thumbnailLocation}`)
            }
        })
    }
}

function uploadTheLocationMappings(path: string): void {
    let jsonContent: string = locationMappingsToJSON()
    fs.writeFileSync(path, jsonContent)
    addAttachmentWithNameAndType(path, "locationMappings")
}

function extractDataAndUpload(dataPath: string): void {
    let metadataObject = getJSONFromDataFileAndWrite(path.join(dataPath, 'metaData.js'),
        path.join(dataPath, '/parsed/metaData.js'));
    addAttachmentWithNameAndType(path.join(dataPath, '/parsed/metaData.js'), 'metadata')
    metadataObject.data.forEach((dataFile: string) => {
        writeJSONFromZippedFile(path.join(dataPath, dataFile), path.join(dataPath, `/parsed/${dataFile}`))
        addAttachmentWithNameAndType(path.join(dataPath, `/parsed/${dataFile}`), 'data')
    });

    getJSONFromDataFileAndWrite(path.join(dataPath, 'tags.js'),
        path.join(dataPath, '/parsed/tags.js'))
    addAttachmentWithNameAndType(path.join(dataPath, '/parsed/tags.js'), 'tags')
}

function writeContentToFile(filePath: string, fileContent: string): void {
    fs.writeFileSync(filePath, fileContent)
}

function writeJSONFromZippedFile(dataFilePath: string, dataWritePath: string) {
    let fileContent: string = getFileContent(dataFilePath)
    let zippedContent: string = fileContent.slice(fileContent.indexOf("'") + 1, fileContent.lastIndexOf("'"))
    let unzippedContent: string = pako.ungzip(Buffer.from(zippedContent, 'base64'), { to: 'string' })
    let scenariosString: string = unzippedContent.slice(unzippedContent.indexOf("["), unzippedContent.lastIndexOf("]") + 1)

    writeContentToFile(dataWritePath, scenariosString)
}

function getJSONFromDataFileAndWrite(dataFilePath: string, dataWritePath: string) {
    let fileContent: string = getFileContent(dataFilePath)
    let jsonContent: string = fileContent.slice(fileContent.indexOf('{'), fileContent.lastIndexOf('}') + 1)
    writeContentToFile(dataWritePath, jsonContent)

    return JSON.parse(jsonContent)
}

function getFileContent(filePath: string): string {
    let fileContent: string = fs.readFileSync(filePath).toString();

    return fileContent
}

function uploadAllTheAttachments(currentPath: string): void {
    if (fs.lstatSync(currentPath).isFile()) {
        addAttachmentWithNameAndType(currentPath, 'attachmentFile')
    } else {
        for (let directoryElement of
            fs.readdirSync(currentPath)) {
            uploadAllTheAttachments(path.join(currentPath, directoryElement))
        }
    }
}

function addAttachmentWithNameAndType(location: string, type: string): void {
    let extension: string = path.extname(location)
    console.log(`##vso[task.addattachment type=${type};name=jgivenFile_${fileCounter}${extension};]` + location)
    if (type === "attachmentFile") {
        if (location.includes(`-thumb${extension}`)) {
            thumbLocations.push(location)
            return
        } else {
            locationMappings.set(`jgivenFile_${fileCounter}${extension}`, location.slice(location.indexOf("attachments/"), location.length))
        }
    }
    fileCounter++
}

function getReportLocation(): string {
    let sourcesDirectory: string = tl.getVariable("build.sourcesdirectory")!
    let jgivenReportLocation: string = tl.getInput("jgivenReportLocation") || 'build/reports/jgiven/html/'

    return path.join(sourcesDirectory, jgivenReportLocation)
}

function locationMappingsToJSON(): string {
    return JSON.stringify(Array.from(locationMappings.entries()))
}

uploadJGivenReport()
