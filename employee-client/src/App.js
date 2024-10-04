// src/App.js
import React, { useState, useEffect } from 'react';
import {
  getRoutes,
  addRoute,
  deleteRoute,
  updateRoute,
  getRouteById,
  searchRoutesByName,
  getRouteWithMaxFrom,
  getCountOfRoutesWithDistanceLowerThan,
} from './apiService';
import { parseStringPromise, Builder } from 'xml2js';

function App() {
  const [routes, setRoutes] = useState([]);
  const [newRoute, setNewRoute] = useState({
    id: '',
    name: '',
    coordinates: {
      x: '',
      y: '',
    },
    creationDate: '',
    from: {
      x: '',
      y: '',
      z: '',
      name: '',
    },
    to: {
      x: '',
      y: '',
      z: '',
      name: '',
    },
    distance: '',
  });
  const [searchValue, setSearchValue] = useState('');
  const [distanceValue, setDistanceValue] = useState('');
  const [countResult, setCountResult] = useState(null);

  useEffect(() => {
    fetchRoutes();
  }, []);

  const fetchRoutes = async () => {
    try {
      const response = await getRoutes();
      const data = await parseStringPromise(response.data);
      const routesArray = data.routes.route || [];
      setRoutes(Array.isArray(routesArray) ? routesArray : [routesArray]);
    } catch (error) {
      console.error('Error fetching routes:', error);
    }
  };

// In your React component
  const handleAddRoute = async () => {
    try {
      await addRoute(newRoute);
      // Refresh routes
    } catch (error) {
      console.error('Error adding route:', error);
    }
  };


  const handleDeleteRoute = async (id) => {
    try {
      await deleteRoute(id);
      fetchRoutes();
    } catch (error) {
      console.error('Error deleting route:', error);
    }
  };

  const handleSearchByName = async () => {
    try {
      const response = await searchRoutesByName(searchValue);
      const data = await parseStringPromise(response.data);
      const routesArray = data.routes.route || [];
      setRoutes(Array.isArray(routesArray) ? routesArray : [routesArray]);
    } catch (error) {
      console.error('Error searching routes:', error);
    }
  };

  const handleGetCount = async () => {
    try {
      const response = await getCountOfRoutesWithDistanceLowerThan(distanceValue);
      const data = await parseStringPromise(response.data);
      const count = data.count || 0;
      setCountResult(count);
    } catch (error) {
      console.error('Error getting count:', error);
    }
  };

  // Similar handlers for update, getRouteWithMaxFrom, etc.

  return (
      <div>
        <h1>Routes</h1>
        <ul>
          {routes.map((route, index) => (
              <li key={index}>
                {route.name} (ID: {route.id})
                <button onClick={() => handleDeleteRoute(route.id)}>Delete</button>
              </li>
          ))}
        </ul>

        <h2>Add New Route</h2>
        <input
            type="text"
            placeholder="Name"
            value={newRoute.name}
            onChange={(e) =>
                setNewRoute({ ...newRoute, name: e.target.value })
            }
        />
        {/* Other input fields for route properties */}
        <button onClick={handleAddRoute}>Add Route</button>

        <h2>Search Routes by Name</h2>
        <input
            type="text"
            placeholder="Search Value"
            value={searchValue}
            onChange={(e) => setSearchValue(e.target.value)}
        />
        <button onClick={handleSearchByName}>Search</button>

        <h2>Get Count of Routes with Distance Lower Than</h2>
        <input
            type="number"
            placeholder="Distance Value"
            value={distanceValue}
            onChange={(e) => setDistanceValue(e.target.value)}
        />
        <button onClick={handleGetCount}>Get Count</button>
        {countResult !== null && <p>Count: {countResult}</p>}
      </div>
  );
}

export default App;
