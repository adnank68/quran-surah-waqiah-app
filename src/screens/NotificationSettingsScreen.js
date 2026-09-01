import React, { useState } from 'react';
import { View, Text, ScrollView, TouchableOpacity, StyleSheet, Modal } from 'react-native';

const NotificationSettingsScreen = () => {
  const [notificationTime, setNotificationTime] = useState('08:00');
  const [notificationsEnabled, setNotificationsEnabled] = useState(true);
  const [showTimePicker, setShowTimePicker] = useState(false);

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>تنظیمات یادآوری</Text>
      </View>

      <View style={styles.settingsCard}>
        <View style={styles.settingItem}>
          <Text style={styles.settingLabel}>فعال کردن یادآوری</Text>
          <TouchableOpacity
            style={[
              styles.toggle,
              notificationsEnabled && styles.toggleActive,
            ]}
            onPress={() => setNotificationsEnabled(!notificationsEnabled)}
          >
            <View
              style={[
                styles.toggleCircle,
                notificationsEnabled && styles.toggleCircleActive,
              ]}
            />
          </TouchableOpacity>
        </View>

        {notificationsEnabled && (
          <>
            <View style={styles.divider} />
            <TouchableOpacity
              style={styles.settingItem}
              onPress={() => setShowTimePicker(true)}
            >
              <Text style={styles.settingLabel}>زمان یادآوری</Text>
              <Text style={styles.timeDisplay}>{notificationTime}</Text>
            </TouchableOpacity>
          </>
        )}
      </View>

      <View style={styles.notificationCard}>
        <Text style={styles.cardTitle}>درباره یادآوری ها</Text>
        <Text style={styles.cardText}>
          شما هر روز در ساعت {notificationTime} یک یادآوری دریافت خواهید کرد تا به یاد داشته باشید سوره واقعه را بخوانید و برنامه ختم خود را ادامه دهید.
        </Text>
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  header: {
    backgroundColor: '#1a472a',
    padding: 20,
    alignItems: 'center',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#ffffff',
  },
  settingsCard: {
    backgroundColor: '#ffffff',
    margin: 15,
    borderRadius: 8,
    overflow: 'hidden',
  },
  settingItem: {
    flexDirection: 'row-reverse',
    padding: 15,
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  settingLabel: {
    fontSize: 16,
    fontWeight: '500',
    color: '#1a472a',
  },
  toggle: {
    width: 50,
    height: 28,
    borderRadius: 14,
    backgroundColor: '#bdc3c7',
    justifyContent: 'center',
    paddingHorizontal: 2,
  },
  toggleActive: {
    backgroundColor: '#27ae60',
  },
  toggleCircle: {
    width: 24,
    height: 24,
    borderRadius: 12,
    backgroundColor: '#ffffff',
  },
  toggleCircleActive: {
    alignSelf: 'flex-end',
  },
  divider: {
    height: 1,
    backgroundColor: '#ecf0f1',
  },
  timeDisplay: {
    fontSize: 14,
    color: '#27ae60',
    fontWeight: '600',
  },
  notificationCard: {
    backgroundColor: '#e8f5e9',
    marginHorizontal: 15,
    marginBottom: 20,
    padding: 15,
    borderRadius: 8,
    borderLeftWidth: 4,
    borderLeftColor: '#27ae60',
  },
  cardTitle: {
    fontSize: 14,
    fontWeight: '600',
    color: '#1a472a',
    marginBottom: 8,
  },
  cardText: {
    fontSize: 13,
    lineHeight: 20,
    color: '#333',
  },
});

export default NotificationSettingsScreen;