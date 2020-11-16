import groovy.json.JsonOutput
import groovy.util.slurpersupport.GPathResult
import org.apache.nifi.flowfile.FlowFile


import java.nio.charset.StandardCharsets
import org.apache.nifi.processor.io.StreamCallback
import org.xml.sax.SAXParseException

def binding = getBinding()
def optionPrettyPrint = binding.hasVariable("prettyPrintJson") ? prettyPrintJson.asBoolean() : false
def optionBatchSize = binding.hasVariable("batchSize") ? batchSize.asInteger() : 10

def flowFiles = session.get(optionBatchSize)
flowFiles.each { flowFile ->
    processFlowFile(flowFile, optionPrettyPrint)
}


void processFlowFile(FlowFile flowFile, boolean prettyPrint) {
    def errorMessage = null
    def outputRelation = REL_SUCCESS
    flowFile = session.write(flowFile, {inputStream, outputStream ->
        try {
            // Parse XML
            def xmlSlurper = new XmlSlurper()
            def inputXmlDoc = xmlSlurper.parse(inputStream)
            
            // New array parsing all EventData attributes
            def primero = (inputXmlDoc.'**'.findAll { node -> node.name() == 'Data' }*.text()).size()
            def mapa = []

            for (i=0; i<primero;i++){
            mapa[i] = (inputXmlDoc.EventData.Data.@Name[i]).text()
            }

            // Transform content
            def outputObj = [:]
            outputObj[inputXmlDoc.name()] = xmlNodeToObject(inputXmlDoc, mapa)

            // Write output content
            def json = JsonOutput.toJson(outputObj)
            if (prettyPrint) {
                json = JsonOutput.prettyPrint(json)
            }
            outputStream.write(json.getBytes(StandardCharsets.UTF_8))
        } catch (SAXParseException ex) {
            errorMessage = ex.getClass().getName() + ": " + ex.getMessage()
            outputRelation = REL_FAILURE
        }
    } as StreamCallback)

    if (errorMessage != null) {
        flowFile = session.putAttribute(flowFile, "xmlToJson.error", errorMessage)
    }
    session.transfer(flowFile, outputRelation);
}


Object xmlNodeToObject(GPathResult node, ArrayList mapa) {
    def nodeName = node.name()
    def attributes = node.attributes()
    def childNodes = node.children()
    def childNodeNames = new HashSet<String>()
    childNodes.each{ childNode -> 
        def childNodeName = childNode.name()
        childNodeNames.add(childNodeName)
    }
    def childTextNodes = node.localText()

    if (childTextNodes.size() > 0) {
        def nodeText = node.text()
        return nodeText
    } else if (childNodes.size() > 1 && attributes.size() == 0 && childNodeNames.size() == 1) {
        def resultList = [:]
        def i = 0
        //def resultList2 = []
        childNodes.each { childNode ->
           
         def childName = childNode.name()
         def childValue = xmlNodeToObject(childNode, mapa)
         if(childName=="Data"){
         resultList[mapa[i]] = childValue
         }
         i = i+1
        }
        attributes.each { attributeName, attributeValue ->
            resultList[attributeName] = attributeValue
        }

        return resultList

    } else if ((childNodes.size() + attributes.size()) > 0) {
        def resultMap = [:]
        attributes.each { attributeName, attributeValue ->
            resultMap[attributeName] = attributeValue
        }
        childNodes.each { childNode ->
            def childName = childNode.name()
            def childValue = xmlNodeToObject(childNode, mapa)
            resultMap[childName] = childValue
        }
        return resultMap
    } else {
        log.warn("Unexpected node structure for node " + nodeName)
    }
}
