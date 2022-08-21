import './App.css';
import axios from 'axios';
import React, {useState, useEffect, useCallback} from "react";
import {useDropzone} from 'react-dropzone';

const Files = ({files}) => {
  return files.map((file,index) => {
    return (
        <div key={index} className="column">
            <img
                src={`data:image/jpeg;base64,${file}`}
            />

        </div>
    );
  });
};

function Dropzone({fetchFiles}) {
  const onDrop = useCallback(acceptedFiles => {
    const file = acceptedFiles[0];
    console.log(file);
    const formData = new FormData();
    formData.append("file", file);
      console.log('formData', formData);
    axios.post(`http://localhost:8080/api/file-store/upload`,
        formData,
        {
          headers:{
            "Content-Type": "multipart/form-data"
          }
        }).then(()=> {
        console.log("file uploaded successfully")
        fetchFiles();
    }).catch(err => {
      console.log(err);
    });
}, []);
  const {getRootProps, getInputProps, isDragActive} = useDropzone({onDrop})

  return (
      <div {...getRootProps({ className: "dropzone" })}>
        <input {...getInputProps()} />
        {
          isDragActive ?
              <p>Drop the files here ...</p> :
              <p>Drag 'n' drop some files here, or click to select files</p>
        }
      </div>
  )
}

function App() {
    const [files, setFiles] = useState([]);

    const fetchFiles = () => {
        axios.get("http://localhost:8080/api/file-store/download").then(res => {
            console.log(res);
            setFiles(res.data);
        });
    };

    useEffect(() => {
        fetchFiles();
    }, []);


    return (
    <div className="App">
        <Dropzone
            fetchFiles={fetchFiles}
            accept="image/*"
            minSize={1024}
            maxSize={3072000}
        />
        <div className="row">
            <Files files={files}/>
        </div>
    </div>
  );
}

export default App;
