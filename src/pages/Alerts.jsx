import React, { useState, useEffect } from 'react';
import { alertService } from '../services/api';
import { 
  ExclamationTriangleIcon,
  BellIcon,
  CheckCircleIcon,
  XMarkIcon
} from '@heroicons/react/24/outline';

export default function Alerts() {
  const [alerts, setAlerts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedFilter, setSelectedFilter] = useState('all');

  useEffect(() => {
    fetchAlerts();
  }, []);

  const fetchAlerts = async () => {
    try {
      const response = await alertService.getAllAlerts();
      setAlerts(response.data);
    } catch (err) {
      setError('Failed to fetch alerts');
      console.error('Error fetching alerts:', err);
    } finally {
      setLoading(false);
    }
  };

  const getAlertIcon = (type) => {
    switch (type) {
      case 'LOW_STOCK':
        return <ExclamationTriangleIcon className="h-6 w-6 text-yellow-500" />;
      case 'EXPIRY':
        return <ExclamationTriangleIcon className="h-6 w-6 text-red-500" />;
      case 'GENERAL':
        return <CheckCircleIcon className="h-6 w-6 text-green-500" />;
      default:
        return <BellIcon className="h-6 w-6 text-blue-500" />;
    }
  };

  const filteredAlerts = alerts.filter(alert => {
    if (selectedFilter === 'all') return true;
    if (selectedFilter === 'unread') return alert.status === 'unread';
    if (selectedFilter === 'high_priority') return alert.priority === 'high';
    return alert.type === selectedFilter.toUpperCase();
  });

  if (loading) return <div>Loading...</div>;
  if (error) return <div className="text-red-500">{error}</div>;

  return (
    <div className="p-6 bg-gray-50">
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-xl font-semibold text-gray-800">Alerts & Notifications</h2>
        <div className="flex items-center space-x-2">
          <span className="text-sm text-gray-500">
            {alerts.filter(a => a.status === 'unread').length} unread
          </span>
          <button className="text-blue-600 text-sm hover:text-blue-800">
            Mark all as read
          </button>
        </div>
      </div>

      <div className="bg-white rounded-lg shadow-sm p-4 mb-6">
        <div className="flex flex-wrap gap-2">
          <button
            onClick={() => setSelectedFilter('all')}
            className={`px-4 py-2 rounded-full text-sm ${
              selectedFilter === 'all'
                ? 'bg-blue-100 text-blue-800'
                : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
            }`}
          >
            All
          </button>
          <button
            onClick={() => setSelectedFilter('unread')}
            className={`px-4 py-2 rounded-full text-sm ${
              selectedFilter === 'unread'
                ? 'bg-blue-100 text-blue-800'
                : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
            }`}
          >
            Unread
          </button>
          <button
            onClick={() => setSelectedFilter('high_priority')}
            className={`px-4 py-2 rounded-full text-sm ${
              selectedFilter === 'high_priority'
                ? 'bg-blue-100 text-blue-800'
                : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
            }`}
          >
            High Priority
          </button>
          <button
            onClick={() => setSelectedFilter('LOW_STOCK')}
            className={`px-4 py-2 rounded-full text-sm ${
              selectedFilter === 'LOW_STOCK'
                ? 'bg-blue-100 text-blue-800'
                : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
            }`}
          >
            Low Stock
          </button>
          <button
            onClick={() => setSelectedFilter('EXPIRY')}
            className={`px-4 py-2 rounded-full text-sm ${
              selectedFilter === 'EXPIRY'
                ? 'bg-blue-100 text-blue-800'
                : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
            }`}
          >
            Expiry
          </button>
        </div>
      </div>

      <div className="space-y-4">
        {filteredAlerts.map((alert) => (
          <div
            key={alert.id}
            className={`bg-white rounded-lg shadow-sm p-4 ${
              alert.status === 'unread' ? 'border-l-4 border-blue-500' : ''
            }`}
          >
            <div className="flex items-start">
              <div className="flex-shrink-0 mr-4">
                {getAlertIcon(alert.type)}
              </div>
              <div className="flex-1 min-w-0">
                <div className="flex items-center justify-between">
                  <h3 className="text-sm font-medium text-gray-900">
                    {alert.type === 'LOW_STOCK'
                      ? 'Low Stock Alert'
                      : alert.type === 'EXPIRY'
                      ? 'Expiry Alert'
                      : 'General Alert'}
                  </h3>
                  <div className="flex items-center space-x-4">
                    <span className="text-xs text-gray-500">
                      {new Date(alert.createdAt).toLocaleString()}
                    </span>
                    <button className="text-gray-400 hover:text-gray-600">
                      <XMarkIcon className="h-5 w-5" />
                    </button>
                  </div>
                </div>
                <p className="mt-1 text-sm text-gray-500">{alert.message}</p>
                {alert.priority === 'high' && (
                  <span className="mt-2 inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800">
                    High Priority
                  </span>
                )}
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}