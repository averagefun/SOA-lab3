// src/components/RouteDetails.js
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router-dom';

function RouteDetails() {
    const { id } = useParams();
    const [route, setRoute] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        axios.get(`/api/routes/${id}`)
            .then(response => {
                setRoute(response.data);
            })
            .catch(error => {
                console.error('There was an error fetching the route!', error);
            });
    }, [id]);

    const handleDelete = () => {
        axios.delete(`/api/routes/${id}`)
            .then(() => {
                navigate('/');
            })
            .catch(error => {
                console.error('There was an error deleting the route!', error);
            });
    };

    if (!route) return <div>Loading...</div>;

    return (
        <div>
            <h1>{route.name}</h1>
            <p>Distance: {route.distance}</p>
            {/* Display other route details as needed */}
            {/* Uncomment and adjust the edit functionality if you have an edit component */}
            {/* <button onClick={() => navigate(`/edit/${id}`)}>Edit</button> */}
            <button onClick={handleDelete}>Delete</button>
            <button onClick={() => navigate('/')}>Back to List</button>
        </div>
    );
}

export default RouteDetails;
