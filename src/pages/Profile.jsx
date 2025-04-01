import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { userService } from '../services/api';
import { UserIcon, KeyIcon, ShieldCheckIcon, ExclamationCircleIcon, CheckCircleIcon } from '@heroicons/react/24/outline';

export default function Profile() {
  const { user } = useAuth();
  const [stats, setStats] = useState({
    totalProducts: 0,
    lowStockItems: 0,
    pendingOrders: 0,
    recentActivity: [],
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  // Password change state
  const [passwordData, setPasswordData] = useState({
    currentPassword: '',
    newPassword: '',
    confirmNewPassword: ''
  });
  const [passwordErrors, setPasswordErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [changeSuccess, setChangeSuccess] = useState(false);

  useEffect(() => {
    fetchUserStats();
  }, []);

  const fetchUserStats = async () => {
    try {
      // Replace with actual API call
      const mockStats = {
        totalProducts: 150,
        lowStockItems: 12,
        pendingOrders: 5,
        recentActivity: [
          {
            id: 1,
            type: 'product_update',
            description: 'Updated product: Rice',
            timestamp: new Date().toISOString(),
          },
          {
            id: 2,
            type: 'order_created',
            description: 'Created new order #1234',
            timestamp: new Date(Date.now() - 86400000).toISOString(),
          },
          {
            id: 3,
            type: 'alert_created',
            description: 'Low stock alert for Sugar',
            timestamp: new Date(Date.now() - 172800000).toISOString(),
          },
        ],
      };
      setStats(mockStats);
    } catch (err) {
      setError('Failed to fetch user stats');
      console.error('Error fetching user stats:', err);
    } finally {
      setLoading(false);
    }
  };

  const handlePasswordChange = (e) => {
    setPasswordData({
      ...passwordData,
      [e.target.name]: e.target.value
    });
    
    // Clear specific field error when typing
    if (passwordErrors[e.target.name]) {
      setPasswordErrors({
        ...passwordErrors,
        [e.target.name]: ''
      });
    }
    
    // Clear success message when typing
    if (changeSuccess) {
      setChangeSuccess(false);
    }
  };

  const validatePasswordForm = () => {
    const errors = {};
    
    if (!passwordData.currentPassword) {
      errors.currentPassword = 'Current password is required';
    }
    
    if (!passwordData.newPassword) {
      errors.newPassword = 'New password is required';
    } else if (passwordData.newPassword.length < 8) {
      errors.newPassword = 'Password must be at least 8 characters long';
    }
    
    if (!passwordData.confirmNewPassword) {
      errors.confirmNewPassword = 'Please confirm your new password';
    } else if (passwordData.newPassword !== passwordData.confirmNewPassword) {
      errors.confirmNewPassword = 'Passwords do not match';
    }
    
    return errors;
  };

  const handleSubmitPasswordChange = async (e) => {
    e.preventDefault();
    
    // Validate form
    const errors = validatePasswordForm();
    if (Object.keys(errors).length > 0) {
      setPasswordErrors(errors);
      return;
    }
    
    setIsSubmitting(true);
    try {
      // In a real app, you would call your API here
      // await userService.changePassword(passwordData);
      
      // For now, we'll just simulate a successful password change
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // Reset form and show success message
      setPasswordData({
        currentPassword: '',
        newPassword: '',
        confirmNewPassword: ''
      });
      setChangeSuccess(true);
    } catch (err) {
      console.error('Error changing password:', err);
      setPasswordErrors({
        form: 'Failed to change password. Please try again.'
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  if (loading) return <div>Loading...</div>;
  if (error) return <div className="text-red-500">{error}</div>;

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="max-w-4xl mx-auto">
        <h1 className="text-2xl font-bold text-gray-900 mb-6">Your Profile</h1>
        
        {/* User Info Card */}
        <div className="bg-white shadow-md rounded-lg p-6 mb-8">
          <div className="flex items-center space-x-4">
            <div className="h-20 w-20 rounded-full bg-indigo-100 flex items-center justify-center">
              <span className="text-2xl font-bold text-indigo-600">
                {user?.firstName?.charAt(0) || ''}
                {user?.lastName?.charAt(0) || ''}
              </span>
            </div>
            <div>
              <h2 className="text-xl font-bold text-gray-900">
                {user?.firstName} {user?.lastName}
              </h2>
              <p className="text-gray-500">{user?.email}</p>
              <p className="text-sm font-medium text-indigo-600 mt-1">
                {user?.role === 'ADMIN' ? 'Administrator' : 'User'}
              </p>
            </div>
          </div>
        </div>
        
        {/* Change Password Section */}
        <div className="bg-white shadow-md rounded-lg p-6 mb-8">
          <div className="flex items-center mb-4">
            <KeyIcon className="h-6 w-6 text-indigo-500 mr-2" />
            <h2 className="text-lg font-semibold text-gray-900">Change Password</h2>
          </div>
          
          {changeSuccess && (
            <div className="mb-4 p-4 bg-green-50 border border-green-200 rounded-md">
              <div className="flex">
                <CheckCircleIcon className="h-5 w-5 text-green-500 mr-2" />
                <p className="text-sm text-green-700">Password successfully changed!</p>
              </div>
            </div>
          )}
          
          {passwordErrors.form && (
            <div className="mb-4 p-4 bg-red-50 border border-red-200 rounded-md">
              <div className="flex">
                <ExclamationCircleIcon className="h-5 w-5 text-red-500 mr-2" />
                <p className="text-sm text-red-700">{passwordErrors.form}</p>
              </div>
            </div>
          )}
          
          <form onSubmit={handleSubmitPasswordChange} className="space-y-4">
            <div>
              <label htmlFor="currentPassword" className="block text-sm font-medium text-gray-700 mb-1">
                Current Password
              </label>
              <input
                type="password"
                id="currentPassword"
                name="currentPassword"
                value={passwordData.currentPassword}
                onChange={handlePasswordChange}
                className={`shadow-sm focus:ring-indigo-500 focus:border-indigo-500 block w-full sm:text-sm border-gray-300 rounded-md ${
                  passwordErrors.currentPassword ? 'border-red-300' : ''
                }`}
              />
              {passwordErrors.currentPassword && (
                <p className="mt-1 text-sm text-red-600">{passwordErrors.currentPassword}</p>
              )}
            </div>
            
            <div>
              <label htmlFor="newPassword" className="block text-sm font-medium text-gray-700 mb-1">
                New Password
              </label>
              <input
                type="password"
                id="newPassword"
                name="newPassword"
                value={passwordData.newPassword}
                onChange={handlePasswordChange}
                className={`shadow-sm focus:ring-indigo-500 focus:border-indigo-500 block w-full sm:text-sm border-gray-300 rounded-md ${
                  passwordErrors.newPassword ? 'border-red-300' : ''
                }`}
              />
              {passwordErrors.newPassword && (
                <p className="mt-1 text-sm text-red-600">{passwordErrors.newPassword}</p>
              )}
            </div>
            
            <div>
              <label htmlFor="confirmNewPassword" className="block text-sm font-medium text-gray-700 mb-1">
                Confirm New Password
              </label>
              <input
                type="password"
                id="confirmNewPassword"
                name="confirmNewPassword"
                value={passwordData.confirmNewPassword}
                onChange={handlePasswordChange}
                className={`shadow-sm focus:ring-indigo-500 focus:border-indigo-500 block w-full sm:text-sm border-gray-300 rounded-md ${
                  passwordErrors.confirmNewPassword ? 'border-red-300' : ''
                }`}
              />
              {passwordErrors.confirmNewPassword && (
                <p className="mt-1 text-sm text-red-600">{passwordErrors.confirmNewPassword}</p>
              )}
            </div>
            
            <div className="flex justify-end">
              <button
                type="submit"
                disabled={isSubmitting}
                className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
              >
                {isSubmitting ? 'Changing...' : 'Change Password'}
              </button>
            </div>
          </form>
        </div>
        
        {/* Stats Cards */}
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Activity Overview</h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <div className="bg-white shadow-md rounded-lg p-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-2">
              Total Products
            </h3>
            <p className="text-3xl font-bold text-indigo-600">
              {stats.totalProducts}
            </p>
          </div>
          <div className="bg-white shadow-md rounded-lg p-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-2">
              Low Stock Items
            </h3>
            <p className="text-3xl font-bold text-yellow-600">
              {stats.lowStockItems}
            </p>
          </div>
          <div className="bg-white shadow-md rounded-lg p-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-2">
              Pending Orders
            </h3>
            <p className="text-3xl font-bold text-green-600">
              {stats.pendingOrders}
            </p>
          </div>
        </div>
        
        {/* Recent Activity */}
        <div className="bg-white shadow-md rounded-lg p-6">
          <h2 className="text-lg font-semibold text-gray-900 mb-4">Recent Activity</h2>
          {stats.recentActivity.length > 0 ? (
            <ul className="space-y-4">
              {stats.recentActivity.map((activity) => (
                <li key={activity.id} className="border-b border-gray-100 pb-3 last:border-b-0 last:pb-0">
                  <div className="flex items-start">
                    <div className="mr-3">
                      {activity.type === 'product_update' && (
                        <div className="h-8 w-8 rounded-full bg-blue-100 flex items-center justify-center">
                          <UserIcon className="h-4 w-4 text-blue-600" />
                        </div>
                      )}
                      {activity.type === 'order_created' && (
                        <div className="h-8 w-8 rounded-full bg-green-100 flex items-center justify-center">
                          <ShieldCheckIcon className="h-4 w-4 text-green-600" />
                        </div>
                      )}
                      {activity.type === 'alert_created' && (
                        <div className="h-8 w-8 rounded-full bg-yellow-100 flex items-center justify-center">
                          <ExclamationCircleIcon className="h-4 w-4 text-yellow-600" />
                        </div>
                      )}
                    </div>
                    <div>
                      <p className="text-sm font-medium text-gray-900">{activity.description}</p>
                      <p className="text-xs text-gray-500">
                        {new Date(activity.timestamp).toLocaleString()}
                      </p>
                    </div>
                  </div>
                </li>
              ))}
            </ul>
          ) : (
            <p className="text-gray-500">No recent activity</p>
          )}
        </div>
      </div>
    </div>
  );
}