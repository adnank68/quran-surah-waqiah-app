import * as Notifications from 'react-native-firebase';
import { KhatmService } from './KhatmService';

export const NotificationService = {
  // Schedule notification
  async scheduleNotification(time) {
    try {
      // TODO: Configure Firebase Cloud Messaging
      console.log('Scheduling notification at:', time);
      const settings = await KhatmService.getSettings();
      const updatedSettings = {
        ...settings,
        notificationTime: time,
        notificationsEnabled: true,
      };
      await KhatmService.saveSettings(updatedSettings);
      return true;
    } catch (error) {
      console.error('Error scheduling notification:', error);
      return false;
    }
  },

  // Cancel notification
  async cancelNotification() {
    try {
      console.log('Canceling notification');
      const settings = await KhatmService.getSettings();
      const updatedSettings = {
        ...settings,
        notificationsEnabled: false,
      };
      await KhatmService.saveSettings(updatedSettings);
      return true;
    } catch (error) {
      console.error('Error canceling notification:', error);
      return false;
    }
  },

  // Request notification permission
  async requestNotificationPermission() {
    try {
      // TODO: Request notification permissions
      return true;
    } catch (error) {
      console.error('Error requesting notification permission:', error);
      return false;
    }
  },
};
