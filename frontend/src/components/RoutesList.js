// src/components/RoutesList.js
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';

function RoutesList() {
    const [routes, setRoutes] = useState([]);

    useEffect(() => {
        axios.get('/api/routes')
            .then(response => {
                setRoutes(response.data);
            })
            .catch(error => {
                console.error('There was an error fetching the routes!', error);
            });
    }, []);

    return (
        <div>
            <h1>Routes</h1>
            <Link to="/add">Add New Route</Link>
            <ul>
                {routes.map(route => (
                    <li key={route.id}>
                        <Link to={`/routes/${route.id}`}>{route.name}</Link>
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default RoutesList;
