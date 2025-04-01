import React from 'react';
import { useApp } from '../context/AppContext';
import {
  ChartBarIcon,
  ExclamationTriangleIcon,
  ClockIcon,
  ArchiveBoxIcon
} from '@heroicons/react/24/outline';

export default function Dashboard() {
  const { stats, alerts, lowStockAlerts, expiryAlerts, isLoading } = useApp();

  const StatCard = ({ title, value, icon: Icon, color }) => (
    <div className={`bg-white rounded-lg shadow-sm p-6 border-l-4 ${color}`}>
      <div className="flex items-center">
        <div className="flex-shrink-0">
          <Icon className="h-8 w-8 text-gray-400" />
        </div>
        <div className="ml-5 w-0 flex-1">
          <dl>
            <dt className="text-sm font-medium text-gray-500 truncate">{title}</dt>
            <dd className="text-3xl font-semibold text-gray-900">{value}</dd>
          </dl>
        </div>
      </div>
    </div>
  );

  if (isLoading) {
    return (
      <div className="p-8 text-center text-gray-500">
        Loading dashboard data...
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-2xl font-semibold text-gray-900 mb-6">Dashboard</h1>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <StatCard
          title="Total Items"
          value={stats.totalItems}
          icon={ArchiveBoxIcon}
          color="border-blue-500"
        />
        <StatCard
          title="Total Quantity"
          value={stats.totalQuantity}
          icon={ChartBarIcon}
          color="border-green-500"
        />
        <StatCard
          title="Low Stock Items"
          value={stats.lowStockCount}
          icon={ExclamationTriangleIcon}
          color="border-yellow-500"
        />
        <StatCard
          title="Expiring Soon"
          value={stats.expiringCount}
          icon={ClockIcon}
          color="border-red-500"
        />
      </div>

      {/* Alerts Section */}
      <div className="bg-white rounded-lg shadow-sm p-6">
        <h2 className="text-lg font-medium text-gray-900 mb-4">Inventory Alerts</h2>
        
        {lowStockAlerts.length === 0 && expiryAlerts.length === 0 ? (
          <p className="text-gray-500">No alerts at this time.</p>
        ) : (
          <div className="space-y-6">
            {/* Low Stock Alerts Section */}
            {lowStockAlerts.length > 0 && (
              <div className="mb-4">
                <h3 className="text-md font-medium text-yellow-700 mb-3 flex items-center">
                  <ExclamationTriangleIcon className="h-5 w-5 mr-2 text-yellow-500" />
                  Low Stock Alerts
                </h3>
                <div className="space-y-3">
                  {lowStockAlerts.map((alert, index) => (
                    <div
                      key={`low-stock-${index}`}
                      className="p-4 rounded-lg bg-yellow-50 text-yellow-700 border border-yellow-200"
                    >
                      <div className="flex">
                        <div className="flex-shrink-0">
                          <ExclamationTriangleIcon className="h-5 w-5 text-yellow-400" />
                        </div>
                        <div className="ml-3">
                          <p className="text-sm font-medium">{alert.message}</p>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* Expiry Alerts Section */}
            {expiryAlerts.length > 0 && (
              <div>
                <h3 className="text-md font-medium text-red-700 mb-3 flex items-center">
                  <ClockIcon className="h-5 w-5 mr-2 text-red-500" />
                  Expiry Alerts
                </h3>
                <div className="space-y-3">
                  {expiryAlerts.map((alert, index) => (
                    <div
                      key={`expiry-${index}`}
                      className="p-4 rounded-lg bg-red-50 text-red-700 border border-red-200"
                    >
                      <div className="flex">
                        <div className="flex-shrink-0">
                          <ClockIcon className="h-5 w-5 text-red-400" />
                        </div>
                        <div className="ml-3">
                          <p className="text-sm font-medium">{alert.message}</p>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}