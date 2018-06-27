/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/


package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"strconv"
	"time"
	"strings"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
)

// MobileTraceChainCode example simple Chaincode implementation
type BarcodeLuggageChainCode struct {
}


// struct for patient
type luggage struct {
	ObjectType         		string 	`json:"luggageType"`
	LuggageScanId           string 	`json:"luggageScanId"`
	LuggageColor 			string 	`json:"luggageColor"`
	Owner 					string 	`json:"owner"`
	Reciver					string	`json:"receiver"`
	Carrier					string	`json:"carrier"`
	Status					string 	`json:"status"`
	Date             		int	`json:"date"`
} 

// ===================================================================================
// Main
// ===================================================================================
func main() {
	err := shim.Start(new(BarcodeLuggageChainCode))
	if err != nil {
		fmt.Printf("Error starting Parts Trace chaincode: %s", err)
	}
}

// Init initializes chaincode
// ===========================
func (t *BarcodeLuggageChainCode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	return shim.Success(nil)
}

// Invoke - Our entry point for Invocations
// ========================================
func (t *BarcodeLuggageChainCode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	function, args := stub.GetFunctionAndParameters()
	fmt.Println("invoke is running " + function)

	// Handle different functions
	if function == "initLuggage" { //create new mobile device
		return t.initLuggage(stub, args)
	}else if function == "getHistoryForRecord" { //get history of values for a record
		return t.getHistoryForRecord(stub, args)
	} else if function == "getCompostiteKey" { //get history of values for a record
		return t.getCompostiteKey(stub, args)
	}else if function == "getProductByRange" { //get history of values for a record
		return t.getProductByRange(stub, args)
	}else if function == "updateLuggageCarrierInfo" { //get history of values for a record
		return t.updateLuggageCarrierInfo(stub, args)
	}else if function == "updateStatus" { //get history of values for a record
		return t.updateStatus(stub, args)
	}
	
	
	fmt.Println("invoke did not find func: " + function) //error
	return shim.Error("Received unknown function invocation")
}

// ============================================================
// initPatient - create a new patient , store into chaincode state
// ============================================================
func (t *BarcodeLuggageChainCode) initLuggage(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	var err error
		
	// @MODIFY_HERE extend to expect 5 arguements, up from 6
	if len(args) < 5 {
		return shim.Error("Incorrect number of arguments. Expecting 5")
	}

	// ==== Input sanitation ====
	fmt.Println("- start init Product")

	if len(args[0]) <= 0 {
		return shim.Error("LuggageScanId must be a non-empty string")
	}
	if len(args[1]) <= 0 {
		return shim.Error("LuggageColor must be a non-empty string")
	}
	if len(args[2]) <= 0 {
		return shim.Error("Owner must be a non-empty string")
	}
	if len(args[3]) <= 0 {
		return shim.Error("Reciver must be a non-empty string")
	}
	if len(args[4]) <= 0 {
		return shim.Error("Carrier must be a non-empty string")
	}
	if len(args[5]) <= 0 {
		return shim.Error("Status must be a non-empty string")
	}
	if len(args[6]) <= 0 {
		return shim.Error("Date must be a non-empty string")
	}
	
	
	luggageScanId  		:=args[0]
	luggageColor 		:=(args[1])
	owner 				:=(args[2])
	reciver				:=(args[3])
	carrier				:=(args[4])
	status 				:=(args[5])
	date,err           	:= strconv.Atoi(args[6])
	if err != nil {
		return shim.Error("date must be a numeric string")
	}			
	// ==== Check if product already exists ====
	luggageAsBytes, err := stub.GetState(luggageScanId)
	if err != nil {
		return shim.Error("Failed to get product: " + err.Error())
	} else if luggageAsBytes != nil {
		return shim.Error("This product already exists: " + luggageScanId)
	}

	// @MODIFY_HERE parts recall fields
	// ==== Create product  and marshal to JSON ====
	objectType := "luggage"
	
	luggage := &luggage{objectType, luggageScanId, luggageColor, owner, reciver, carrier, status, date}
	luggageJSONasBytes, err := json.Marshal(luggage)
	if err != nil {
		return shim.Error(err.Error())
	}

	// === Save product to state ===
	err = stub.PutState(luggageScanId, luggageJSONasBytes)
	if err != nil {
		return shim.Error(err.Error())
	}
	//  ==== Index the product based on the scanid, productname, productmanufacturer, and productowner
	//  An 'index' is a normal key/value entry in state.
	//  The key is a composite key, with the elements that you want to range query on listed first.
	//  In our case, the composite key is based on product~details
	//  This will enable very efficient state range queries based on composite keys matching product~details~*

	luggageIndex := "luggage~details"
	luggageIndexKey, err := stub.CreateCompositeKey(luggageIndex, []string{ luggage.LuggageScanId, luggage.Owner, luggage.Carrier, luggage.Reciver})
	if err != nil {
		return shim.Error(err.Error())
	}
	//  Save index entry to state. Only the key name is needed, no need to store a duplicate copy of the marble.
	//  Note - passing a 'nil' value will effectively delete the key from state, therefore we pass null character as value
	value := []byte{0x00}
	stub.PutState(luggageIndexKey, value)

	// ==== Visit part saved and indexed. Return success ====
	fmt.Println("- end init product")
	return shim.Success(nil)
}

//=================================================================================================================================================================================================

 
// // ===========================================================================================
// // getHistoryForRecord returns the histotical state transitions for a given key of a record
// // ===========================================================================================
func (t *BarcodeLuggageChainCode) getHistoryForRecord(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	if len(args) < 1 {
		return shim.Error("Incorrect number of arguments. Expecting 1")
	}

	recordKey := args[0]

	fmt.Printf("- start getHistoryForRecord: %s\n", recordKey)

	resultsIterator, err := stub.GetHistoryForKey(recordKey)
	if err != nil {
		return shim.Error(err.Error())
	}
	defer resultsIterator.Close()

	// buffer is a JSON array containing historic values for the key/value pair
	var buffer bytes.Buffer
	buffer.WriteString("[")

	bArrayMemberAlreadyWritten := false
	for resultsIterator.HasNext() {
		response, err := resultsIterator.Next()
		if err != nil {
			return shim.Error(err.Error())
		}
		// Add a comma before array members, suppress it for the first array member
		if bArrayMemberAlreadyWritten == true {
			buffer.WriteString(",")
		}
		buffer.WriteString("{\"TxId\":")
		buffer.WriteString("\"")
		buffer.WriteString(response.TxId)
		buffer.WriteString("\"")

		buffer.WriteString(", \"Value\":")
		// if it was a delete operation on given key, then we need to set the
		//corresponding value null. Else, we will write the response.Value
		if response.IsDelete {
			buffer.WriteString("null")
		} else {
			buffer.WriteString(string(response.Value))
		}

		buffer.WriteString(", \"Timestamp\":")
		buffer.WriteString("\"")
		buffer.WriteString(time.Unix(response.Timestamp.Seconds, int64(response.Timestamp.Nanos)).String())
		buffer.WriteString("\"")


		buffer.WriteString("}")
		bArrayMemberAlreadyWritten = true
	}
	buffer.WriteString("]")

	fmt.Printf("- getHistoryForRecord returning:\n%s\n", buffer.String())

	return shim.Success(buffer.Bytes())
}


//============================================================================================
// Searches based on the composite key and returns all the data requested for based on 
// search string and the key like, Search String (product~rx~doctor, patient~visit~doctor) and 
// Key could be Patient ID or Doctor ID or Visit ID 
// Work around in case, couchDB cannot be configured
//============================================================================================

func (t *BarcodeLuggageChainCode) getCompostiteKey(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	if len(args) < 2 {
		return shim.Error("Incorrect number of arguments. Expecting 2")
	}

	keyString := args[0]
	keyToSearch := args[1]
	
	resultsIterator, err := stub.GetStateByPartialCompositeKey(keyString, []string{keyToSearch})
	if err != nil {
		return shim.Error(err.Error())
	}
	defer resultsIterator.Close()

	// buffer is a JSON array containing QueryResults
	var buffer bytes.Buffer
	buffer.WriteString("[")

	bArrayMemberAlreadyWritten := false
	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		if err != nil {
			return shim.Error(err.Error())
		}
		objectType, compositeKeyParts, err := stub.SplitCompositeKey(queryResponse.Key)
		if err != nil {
			return shim.Error(err.Error())
		}
		key1 := compositeKeyParts[0]
		key2 := compositeKeyParts[1]
		key3 := compositeKeyParts[2]
		key4 := compositeKeyParts[3]
		// Add a comma before array members, suppress it for the first array member
		if bArrayMemberAlreadyWritten == true {
			buffer.WriteString(",")
		}
		buffer.WriteString("{\"key1\":")
		buffer.WriteString("\"")
		buffer.WriteString(string(key1))
		buffer.WriteString("\"")

		buffer.WriteString(",\"key2\":")
		buffer.WriteString("\"")
		buffer.WriteString(string(key2))
		buffer.WriteString("\"")

		buffer.WriteString(",\"key3\":")
		buffer.WriteString("\"")
		buffer.WriteString(string(key3))
		buffer.WriteString("\"")

		buffer.WriteString(",\"key4\":")
		buffer.WriteString("\"")
		buffer.WriteString(string(key4))
		buffer.WriteString("\"")

		buffer.WriteString(",\"objectType\":")
		buffer.WriteString("\"")
		buffer.WriteString(string(objectType))
		buffer.WriteString("\"")

		// buffer.WriteString(", \"Record\":")
		// // Record is a JSON object, so we write as-is
		// buffer.WriteString(string(queryResponse.Value))
		buffer.WriteString("}")
		bArrayMemberAlreadyWritten = true
	}
	buffer.WriteString("]")

	fmt.Printf("- getCompositeKey queryResult:\n%s\n", buffer.String())

	return shim.Success(buffer.Bytes())
}

func (t *BarcodeLuggageChainCode) getProductByRange(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	if len(args) < 2 {
		return shim.Error("Incorrect number of arguments. Expecting 2")
	}

	startKey := args[0]
	endKey := args[1]

	resultsIterator, err := stub.GetStateByRange(startKey, endKey)
	if err != nil {
		return shim.Error(err.Error())
	}
	defer resultsIterator.Close()

	// buffer is a JSON array containing QueryResults
	var buffer bytes.Buffer
	buffer.WriteString("[")

	bArrayMemberAlreadyWritten := false
	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		if err != nil {
			return shim.Error(err.Error())
		}
		// Add a comma before array members, suppress it for the first array member
		if bArrayMemberAlreadyWritten == true {
			buffer.WriteString(",")
		}
		buffer.WriteString("{\"Key\":")
		buffer.WriteString("\"")
		buffer.WriteString(queryResponse.Key)
		buffer.WriteString("\"")

		buffer.WriteString(", \"Record\":")
		// Record is a JSON object, so we write as-is
		buffer.WriteString(string(queryResponse.Value))
		buffer.WriteString("}")
		bArrayMemberAlreadyWritten = true
	}
	buffer.WriteString("]")

	fmt.Printf("- getproduct queryResult:\n%s\n", buffer.String())

	return shim.Success(buffer.Bytes())
}
//---------------------------------------------------------------------------------------------------------------------------------------------------
func (t *BarcodeLuggageChainCode) updateLuggageCarrierInfo(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) < 2 {
		return shim.Error("Incorrect number of arguments. Expecting 2")
	}
	scanId := args[0]
	carrierInfo := strings.ToLower(args[1])
	
	
	// attempt to get the current luggage object by id.
	// if sucessful, returns us a byte array we can then us JSON.parse to unmarshal
	fmt.Println("Updating carrier info for: " + scanId)
	luggageAsBytes, err := stub.GetState(scanId)
	if err != nil {
		return shim.Error("Failed to get luggage details: " + err.Error())
	} else if luggageAsBytes == nil {
		return shim.Error("No Luggage is created for the luggage id: " + scanId)
	}

	updateLuggageCarrier := luggage{}
	err = json.Unmarshal(luggageAsBytes, &updateLuggageCarrier) //unmarshal it aka JSON.parse()
	if err != nil {
		return shim.Error(err.Error())
	}

	updateLuggageCarrier.Carrier = carrierInfo 

	luggageJSONasBytes, _ := json.Marshal(updateLuggageCarrier)
	err = stub.PutState(scanId, luggageJSONasBytes) //rewrite visit with Pharmacy bill details
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success(nil)
}
//-----------------------------------------------------------------------------------------------------------------------------------------------------
func (t *BarcodeLuggageChainCode) updateStatus(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) < 3 {
		return shim.Error("Incorrect number of arguments. Expecting 3")
	}
	scanId := args[0]
	status := strings.ToLower(args[1])
	carrier := strings.ToLower(args[2])
	date,err := strconv.Atoi(args[3])
	if err != nil {
		return shim.Error("date must be a numeric string")
	}	
	
	
	// attempt to get the current luggage object by id.
	// if sucessful, returns us a byte array we can then us JSON.parse to unmarshal
	fmt.Println("Updating carrier info for: " + scanId)
	luggageAsBytes, err := stub.GetState(scanId)
	if err != nil {
		return shim.Error("Failed to get luggage details: " + err.Error())
	} else if luggageAsBytes == nil {
		return shim.Error("No Luggage is created for the luggage id: " + scanId)
	}

	updateLuggageCarrier := luggage{}
	err = json.Unmarshal(luggageAsBytes, &updateLuggageCarrier) //unmarshal it aka JSON.parse()
	if err != nil {
		return shim.Error(err.Error())
	}

	updateLuggageCarrier.Status = status 
	updateLuggageCarrier.Carrier = carrier
	updateLuggageCarrier.Date = date

	luggageJSONasBytes, _ := json.Marshal(updateLuggageCarrier)
	err = stub.PutState(scanId, luggageJSONasBytes) //rewrite visit with Pharmacy bill details
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success(nil)
}
