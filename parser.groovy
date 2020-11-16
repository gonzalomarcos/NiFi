import java.util.regex.Pattern
import java.util.regex.Matcher
import org.apache.nifi.flowfile.FlowFile


def flowFileList = session.get(20)
if(flowFileList.isEmpty()) return


flowFileList.each { flowFile ->

try {
output = [:]
//get attribute name
text = flowFile.getAttribute('msg')
event = flowFile.getAttribute('event_id')


regex = text.contains("Changes: ")
if(regex){
regex = text =~ /.*?Changes\:\s+(.*)/
regex.find()

//create new attribute with the first group of the regex
output.put("changes",regex.group(1))}

flowFile = session.putAllAttributes(flowFile, output)
session.transfer(flowFile, REL_SUCCESS)

} catch (Exception ex){
           session.transfer(flowFile, REL_FAILURE);
       }
}
