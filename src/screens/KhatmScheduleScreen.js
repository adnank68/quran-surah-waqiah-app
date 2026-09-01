import React, { useState, useEffect } from 'react';
import { View, Text, ScrollView, TouchableOpacity, StyleSheet, FlatList } from 'react-native';

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

  const renderDayItem = ({ item }) => (
    <TouchableOpacity
      style={[
        styles.dayBox,
        item.completed && styles.dayBoxCompleted,
      ]}
      onPress={() => toggleDay(item.id)}
    >
      <Text
        style={[
          styles.dayText,
          item.completed && styles.dayTextCompleted,
        ]}
      >
        روز {item.day}
      </Text>
      {item.completed && (
        <Text style={styles.checkmark}>✓</Text>
      )}
    </TouchableOpacity>
  );

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>برنامه‌ی خِتم دوره‌ای</Text>
        <Text style={styles.progress}>
          {completedDays} روز از {totalDays} روز تمام‌شده
        </Text>
      </View>

      <View style={styles.statsSection}>
        <View style={styles.statBox}>
          <Text style={styles.statNumber}>{completedDays}</Text>
          <Text style={styles.statLabel}>تمام‌شده</Text>
        </View>
        <View style={styles.statBox}>
          <Text style={styles.statNumber}>{totalDays - completedDays}</Text>
          <Text style={styles.statLabel}>باقی‌مانده</Text>
        </View>
        <View style={styles.statBox}>
          <Text style={styles.statNumber}>{Math.round((completedDays / totalDays) * 100)}%</Text>
          <Text style={styles.statLabel}>پیشرفت</Text>
        </View>
      </View>

      <View style={styles.daysSection}>
        <Text style={styles.sectionTitle}>تقویم خِتم</Text>
        <FlatList
          data={days}
          renderItem={renderDayItem}
          keyExtractor={item => item.id.toString()}
          numColumns={5}
          scrollEnabled={false}
          columnWrapperStyle={styles.columnWrapper}
        />
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
  progress: {
    fontSize: 14,
    color: '#bdc3c7',
    marginTop: 10,
  },
  statsSection: {
    flexDirection: 'row-reverse',
    paddingHorizontal: 10,
    marginTop: 15,
    marginBottom: 15,
  },
  statBox: {
    flex: 1,
    backgroundColor: '#ffffff',
    marginHorizontal: 5,
    padding: 15,
    borderRadius: 8,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 3,
    elevation: 3,
  },
  statNumber: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#27ae60',
  },
  statLabel: {
    fontSize: 12,
    color: '#95a5a6',
    marginTop: 5,
  },
  daysSection: {
    paddingHorizontal: 10,
    marginBottom: 20,
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#1a472a',
    marginBottom: 15,
    textAlign: 'right',
  },
  columnWrapper: {
    justifyContent: 'space-between',
    marginBottom: 10,
  },
  dayBox: {
    width: '18%',
    aspectRatio: 1,
    backgroundColor: '#ffffff',
    borderRadius: 8,
    justifyContent: 'center',
    alignItems: 'center',
    borderWidth: 1,
    borderColor: '#ddd',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
    elevation: 1,
  },
  dayBoxCompleted: {
    backgroundColor: '#27ae60',
    borderColor: '#229954',
  },
  dayText: {
    fontSize: 11,
    fontWeight: '600',
    color: '#1a472a',
  },
  dayTextCompleted: {
    color: '#ffffff',
  },
  checkmark: {
    fontSize: 16,
    color: '#ffffff',
    marginTop: 2,
  },
});

export default KhatmScheduleScreen;