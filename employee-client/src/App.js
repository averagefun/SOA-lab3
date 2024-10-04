import React, { useState } from 'react';

function App() {
  const [employee, setEmployee] = useState(null);
  const [id, setId] = useState('');

  const getEmployeeById = async () => {
    const url = 'http://localhost:8080/employee-service';
    const soapRequest = `
      <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:empl="http://employee.jaxwsserver.gre90r.de/">
        <soapenv:Header/>
        <soapenv:Body>
          <empl:getEmployeeById>
            <arg0>${id}</arg0>
          </empl:getEmployeeById>
        </soapenv:Body>
      </soapenv:Envelope>
    `;

    try {
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'text/xml;charset=UTF-8',
          SOAPAction: '',
        },
        body: soapRequest,
      });

      const responseText = await response.text();
      console.log(responseText);

      const parser = new DOMParser();
      const xmlDoc = parser.parseFromString(responseText, 'text/xml');
      const employeeId = xmlDoc.getElementsByTagName('id')[0]?.textContent;
      const employeeName = xmlDoc.getElementsByTagName('name')[0]?.textContent;

      if (employeeId && employeeName) {
        setEmployee({ id: employeeId, name: employeeName });
      } else {
        setEmployee(null);
      }
    } catch (error) {
      console.error('Error fetching employee:', error);
    }
  };

  return (
      <div style={{ padding: '20px' }}>
        <h1>Employee Service</h1>
        <input
            type="text"
            value={id}
            onChange={(e) => setId(e.target.value)}
            placeholder="Enter Employee ID"
        />
        <button onClick={getEmployeeById}>Get Employee</button>
        {employee ? (
            <div>
              <h2>Employee Details:</h2>
              <p>ID: {employee.id}</p>
              <p>Name: {employee.name}</p>
            </div>
        ) : (
            <p>No employee data</p>
        )}
      </div>
  );
}

export default App;
