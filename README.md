# NiFi

### XML2Json 
* https://github.com/BatchIQ/nifi-scripting-samples/blob/master/src/test/resources/executescript/content/xml-to-json/xmlToJson.groovy 
  * taken from BatchIQ and modified to parse XML WEL event logs and get a json, see examples
<details>
 <summary>Input format example - XML</summary>
 <p>
  
  ```groovy
<?xml version="1.0" encoding="UTF-8"?>
<Event xmlns="http://schemas.microsoft.com/win/2004/08/events/event">
  <System>
    <Provider Name="Microsoft-Windows-Security-Auditing" Guid="{54849625-5478-4994-A5BA-3E3B0328C30D}" />
    <EventID>4634</EventID>
    <Version>0</Version>
    <Level>0</Level>
    <Task>12545</Task>
    <Opcode>0</Opcode>
    <Keywords>0x8020000000000000</Keywords>
    <TimeCreated SystemTime="2015-09-09T02:27:57.877205900Z" />
    <EventRecordID>230019</EventRecordID>
    <Correlation />
    <Execution ProcessID="516" ThreadID="832" />
    <Channel>Security</Channel>
    <Computer>DC01.contoso.local</Computer>
    <Security />
  </System>
  <EventData>
		 <Data Name="TargetUserSid">S-1-5-90-1</Data> 
		 <Data Name="TargetUserName">DWM-1</Data> 
		 <Data Name="TargetDomainName">Window Manager</Data> 
		 <Data Name="TargetLogonId">0x1a0992</Data> 
		 <Data Name="LogonType">2</Data> 
  </EventData>
  <RenderingInfo Culture="es-ES">
    <Message>An account was logged off.  This event is generated when a logon session is destroyed. It may be positively correlated with a logon event using the Logon ID value. Logon IDs are only unique between reboots on the same computer.</Message>
    <Level>Information</Level>
    <Task>Logoff</Task>
    <Opcode>Info</Opcode>
    <Channel>Security</Channel>
    <Provider>Microsoft Windows security auditing.</Provider>
    <Keywords>
      <Keyword>Audit Success</Keyword>
    </Keywords>
  </RenderingInfo>
</Event>
  ```
 </details>

<details>
 <summary>Output format example - JSON</summary>
 <p>
  
  ```groovy
{
  "Event": {
    "System": {
      "Provider": {
        "Guid": "{54849625-5478-4994-A5BA-3E3B0328C30D}",
        "Name": "Microsoft-Windows-Security-Auditing"
      },
      "EventID": "4634",
      "Version": "0",
      "Level": "0",
      "Task": "12545",
      "Opcode": "0",
      "Keywords": "0x8020000000000000",
      "TimeCreated": {
        "SystemTime": "2015-09-09T02:27:57.877205900Z"
      },
      "EventRecordID": "230019",
      "Correlation": null,
      "Execution": {
        "ThreadID": "832",
        "ProcessID": "516"
      },
      "Channel": "Security",
      "Computer": "DC01.contoso.local",
      "Security": null
    },
    "EventData": {
      "TargetUserSid": "-1-5-90-1",
      "TargetUserName": "DWM-1",
      "TargetDomainName": "Window Manager",
      "TargetLogonId": "0x1a0992",
      "LogonType": "2"
    },
    "RenderingInfo": {
      "Culture": "es-ES",
      "Message": "An account was logged off.   This event is generated when a logon session is destroyed. It may be positively correlated with a logon event using the Logon ID value. Logon IDs are only unique between reboots on the same computer.",
      "Level": "Information",
      "Task": "Logoff",
      "Opcode": "Info",
      "Channel": "Security",
      "Provider": "Microsoft Windows security auditing.",
      "Keywords": {
        "Keyword": "Audit Success"
      }
    }
  }
}
  ```
</details>

### Groovy parser
* Parse all type of logs through a groovy script, if regex fails, the flowfile will be transferred to failure, just tweak it and add your regex to parse events.

