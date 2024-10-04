// src/components/RouteForm.js
import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

function RouteForm() {
    const [name, setName] = useState('');
    const [distance, setDistance] = useState('');
    const navigate = useNavigate();

    const handleSubmit = (e) => {
        e.preventDefault();
        const newRoute = {
            name,
            distance: parseInt(distance),
            coordinates: { x: 0, y: 0 }, // Adjust as needed
            from: { x: 0, y: 0, z: 0, name: '' },
            to: { x: 0, y: 0, z: 0, name: '' },
        };

        axios.post('/api/routes', newRoute)
            .then(response => {
                navigate('/');
            })
            .catch(error => {
                console.error('There was an error creating the route!', error);
            });
    };

    return (
        <div>
            <h1>Add New Route</h1>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Route Name:</label>
                    <input
                        type="text"
                        value={name}
                        onChange={e => setName(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Distance:</label>
                    <input
                        type="number"
                        value={distance}
                        onChange={e => setDistance(e.target.value)}
                        required
                        min="2"
                    />
                </div>
                {/* Add more fields for Coordinates, From, and To as needed */}
                <button type="submit">Add Route</button>
            </form>
        </div>
    );
}

export default RouteForm;
