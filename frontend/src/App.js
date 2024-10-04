// src/App.js
import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import RoutesList from './components/RoutesList';
import RouteForm from './components/RouteForm';
import RouteDetails from './components/RouteDetails';

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<RoutesList />} />
                <Route path="/add" element={<RouteForm />} />
                <Route path="/routes/:id" element={<RouteDetails />} />
                {/* Add other routes as needed */}
            </Routes>
        </Router>
    );
}

export default App;
