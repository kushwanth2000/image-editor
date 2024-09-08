import logo from './logo.svg';
import './App.css';


import React, {Component} from "react";



class App extends Component {


    state = {
        // Initially, no file is selected
        selectedFiles: null,
        coordinateString: 'null',
        response: null,
        locationData: {
            latRef: 'null',
            lonRef: 'null',
            latitude: 'null',
            longitude: 'null'
        }
    };



    // On file select (from the pop up)
    onFileChange = (event) => {
        // Update the state
        console.log("file changed: {}", event.target.files)
        console.log(event.target.files[0])
        console.log(event.target.files[1])
        console.log(event.target.files.length);
        if (event.target.files.length >= 0) {
            console.log("inside if ");
            this.setState({
                selectedFiles: event.target.files
            });
        }
        console.log("successful: {} ", this.state.selectedFiles)
    };


    // On file upload (click the upload button)
    onFileUpload = () => {

        const fileImageNames = Array.from(this.state.selectedFiles).map(file => file.name);

        console.log("fileImageNames: {}", fileImageNames)

        const requestBody = {
            latRef: this.state.locationData.latRef,
            lonRef: this.state.locationData.lonRef,
            latitude: this.state.locationData.latitude,
            longitude: this.state.locationData.longitude,
            fileImageName: fileImageNames,
        };

        const httpRequestBody = JSON.stringify(requestBody);



        const xhr = new XMLHttpRequest();
        xhr.open('POST', 'http://localhost:8080/updateImageLocation');
        xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
        xhr.onload = function () {
            if (xhr.status === 200) {
                console.log("Data updated successfully {}", fileImageNames)
                console.log("response {}", xhr.responseText);
                this.state.response = xhr.responseText;
            } else {
                this.state.response = "api failed with " + xhr.status;
                console.log(xhr.response)
            }
        };
        console.log("local RequestBody: {}", httpRequestBody)
        xhr.send(httpRequestBody)
    };
    // File content to be displayed after
    // file upload is complete
    fileData = () => {

        if (this.state.selectedFiles && this.state.selectedFiles.length >= 0) {
            console.log("selectedFiles: {} ", this.state.selectedFiles)

            return Array.from(this.state.selectedFiles).map(file => {
                return (
                    <div>
                        <h2>File Details:</h2>
                        <p>File Name: {file.name}</p>
                        <p>File Path: {file.mozFullPath}</p>

                        <p>File Type: {file.type}</p>

                        <p>
                            Last Modified:
                            {file.lastModifiedDate.toDateString()}
                        </p>
                    </div>
                );
            });


            // return (
            //
            //     {
            //         this.state.selectedFiles.map(selectedFile => {
            //                 console.log("selectedFile: {} ", selectedFile)
            //
            //                 return (<div>
            //                     <h2>File Details:</h2>
            //                     <p>File Name: {selectedFile.name}</p>
            //
            //                     <p>File Type: {selectedFile.type}</p>
            //
            //                     <p>
            //                         Last Modified:
            //                         {selectedFile.lastModifiedDate.toDateString()}
            //                     </p>
            //                 </div>);
            //
            //             }
            //         )
            //     })
        } else {
            return (
                <div>
                    <br/>
                    <h4>Choose before Pressing the Upload button</h4>
                </div>
            );
        }
        ;
    };


    render() {

        return (<div className="App">
                {/*// <header className="App-header">*/}
                {/*// <img src={logo} className="App-logo" alt="logo"/>*/}
                {/*// <p>*/}
                {/*// Edit <code>src/App.js</code> and save to reload.*/}
                {/*// </p>*/}
                {/*// <a*/}
                {/*//          className="App-link"*/}
                {/*//          href="https://reactjs.org"*/}
                {/*//          target="_blank"*/}
                {/*//          rel="noopener noreferrer"*/}
                {/*//        >*/}
                {/*//          Learn React*/}
                {/*//        </a>*/}
                {/*//      </header>*/}


                <h1>GeeksforGeeks</h1>
                <h3>File Upload using React!</h3>
                <div>
                    <input type="file" onChange={this.onFileChange} multiple={true}/>
                    <button onClick={this.onFileUpload}>Upload!</button>
                </div>
                <form>
                    <label>Enter coordinates:
                        <input
                            type="text"
                            value={this.state.coordinateString}
                            onChange={(e) => {
                                console.log("coordinateString is changed to {}", e.target.value)
                                this.state.coordinateString = e.target.value;

                                var coordinatesPostSplit = e.target.value.split(',');

                                console.log("coordinatesPostSplit {}", coordinatesPostSplit)

                                this.state.locationData.latitude = coordinatesPostSplit[0];
                                this.state.locationData.longitude = coordinatesPostSplit[1];
                                this.state.locationData.latRef = '';
                                this.state.locationData.lonRef = '';




                                // const regex = /(\d{1,2}°\d{1,2}'\d{1,2}\.\d+")(N|S)\s(\d{1,3}°\d{1,2}'\d{1,2}\.\d+")(E|W)/;
                                // const match = coordinateString1.match(regex);
                                //
                                // if (match) {
                                //     const [_, lat, latDir, lon, lonDir] = match;
                                //     this.state.locationData.latitude = `${lat}`;
                                //     this.state.locationData.longitude = `${lon}`;
                                //     this.state.locationData.latRef = latDir;
                                //     this.state.locationData.lonRef = lonDir;
                                // } else {
                                //     this.state.locationData.latitude = 'Invalid format';
                                //     this.state.locationData.longitude = 'Invalid format';
                                //     this.state.locationData.latRef = '';
                                //     this.state.locationData.lonRef = ''
                                // }
                                this.forceUpdate()
                            }
                            }
                        />
                    </label>
                </form>
                <div>
                    <h1>Coordinates</h1>
                    <div>
                        <div>
                            <strong>Latitude:</strong> <span> {this.state.locationData.latitude}</span>
                            <span>  </span>
                            <strong>Direction:</strong> <span> {this.state.locationData.latRef}</span>
                        </div>
                        <div>
                            <strong>Longitude:</strong> <span>{this.state.locationData.longitude}</span>
                            <span>  </span>
                            <strong>Direction:</strong> <span>{this.state.locationData.lonRef}</span>
                        </div>
                    </div>
                </div>

                {this.fileData()}
            </div>
        );
    }
}

export default App;