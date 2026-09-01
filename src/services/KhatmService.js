import AsyncStorage from '@react-native-async-storage/async-storage';

const STORAGE_KEY_KHATM_DATA = 'khatm_data';
const STORAGE_KEY_SETTINGS = 'khatm_settings';
const STORAGE_KEY_PROGRESS = 'khatm_progress';

export const KhatmService = {
  // Save khatm schedule
  async saveKhatmSchedule(schedule) {
    try {
      await AsyncStorage.setItem(
        STORAGE_KEY_KHATM_DATA,
        JSON.stringify(schedule)
      );
      return true;
    } catch (error) {
      console.error('Error saving khatm schedule:', error);
      return false;
    }
  },

  // Get khatm schedule
  async getKhatmSchedule() {
    try {
      const data = await AsyncStorage.getItem(STORAGE_KEY_KHATM_DATA);
      return data ? JSON.parse(data) : null;
    } catch (error) {
      console.error('Error getting khatm schedule:', error);
      return null;
    }
  },

  // Mark day as completed
  async markDayCompleted(dayId) {
    try {
      const schedule = await this.getKhatmSchedule();
      if (schedule) {
        const updatedDays = schedule.days.map(d => {
          if (d.id === dayId) {
            return { ...d, completed: true, completedDate: new Date().toISOString() };
          }
          return d;
        });
        await this.saveKhatmSchedule({
          ...schedule,
          days: updatedDays,
        });
        return true;
      }
      return false;
    } catch (error) {
      console.error('Error marking day completed:', error);
      return false;
    }
  },

  // Get progress percentage
  async getProgress() {
    try {
      const schedule = await this.getKhatmSchedule();
      if (schedule && schedule.days) {
        const completed = schedule.days.filter(d => d.completed).length;
        return (completed / schedule.days.length) * 100;
      }
      return 0;
    } catch (error) {
      console.error('Error getting progress:', error);
      return 0;
    }
  },

  // Save settings
  async saveSettings(settings) {
    try {
      await AsyncStorage.setItem(
        STORAGE_KEY_SETTINGS,
        JSON.stringify(settings)
      );
      return true;
    } catch (error) {
      console.error('Error saving settings:', error);
      return false;
    }
  },

  // Get settings
  async getSettings() {
    try {
      const data = await AsyncStorage.getItem(STORAGE_KEY_SETTINGS);
      return data ? JSON.parse(data) : null;
    } catch (error) {
      console.error('Error getting settings:', error);
      return null;
    }
  },
};
