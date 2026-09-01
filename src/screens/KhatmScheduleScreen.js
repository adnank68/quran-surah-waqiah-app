import React, { useState, useEffect } from 'react';
import { View, Text, ScrollView, TouchableOpacity, StyleSheet, CheckBox } from 'react-native';
import { useSelector, useDispatch } from 'react-redux';

const KhatmScheduleScreen = () => {
  const [days, setDays] = useState([]);
  const [totalDays, setTotalDays] = useState(30);
  const [completedDays, setCompletedDays] = useState(0);

  useEffect(() => {
    initializeDays();
  }, []);

  const initializeDays = () => {
    const newDays = Array.from({ length: totalDays }, (_, i) => ({
      id: i,
      day: i + 1,
      completed: false,
    }));
    setDays(newDays);
  };

  const toggleDay = (dayId) => {
    setDays(days.map(d => {
      if (d.id === dayId) {
        const newCompleted = !d.completed;
        if (newCompleted) {
          setCompletedDays(completedDays + 1);
        } else {
          setCompletedDays(completedDays - 1);
        }
        return { ...d, completed: newCompleted };
      }
      return d;
    }));
  };

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>برنامه‌ی ختم دوره‌ای</Text>
        <Text style={styles.progress}>
          {completedDays} روز از {totalDays} روز تمام‌شده
        </Text>
      </View>

      <View style={styles.daysGrid}>
        {days.map(day => (
          <TouchableOpacity
            key={day.id}
            style={[
              styles.dayBox,
              day.completed && styles.dayBoxCompleted,
            ]}
            onPress={() => toggleDay(day.id)}
          >
            <Text
              style={[
                styles.dayText,
                day.completed && styles.dayTextCompleted,
              ]}
            >
              روز {day.day}
            </Text>
            {day.completed && (
              <Text style={styles.checkmark}>✓</Text>
            )}
          </TouchableOpacity>
        ))}
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
    backgroundColor: '#2c3e50',
    padding: 20,
    alignItems: 'center',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#ffffff',
  },
  progress: {
    fontSize: 14,
    color: '#bdc3c7',
    marginTop: 10,
  },
  daysGrid: {
    flexDirection: 'row-reverse',
    flexWrap: 'wrap',
    padding: 10,
    justifyContent: 'space-between',
  },
  dayBox: {
    width: '30%',
    aspectRatio: 1,
    backgroundColor: '#ffffff',
    borderRadius: 8,
    margin: 5,
    justifyContent: 'center',
    alignItems: 'center',
    borderWidth: 1,
    borderColor: '#ddd',
  },
  dayBoxCompleted: {
    backgroundColor: '#4CAF50',
    borderColor: '#45a049',
  },
  dayText: {
    fontSize: 14,
    fontWeight: '600',
    color: '#2c3e50',
  },
  dayTextCompleted: {
    color: '#ffffff',
  },
  checkmark: {
    fontSize: 20,
    color: '#ffffff',
    marginTop: 5,
  },
});

export default KhatmScheduleScreen;
