import React, { useState, useEffect } from 'react';
import { View, Text, ScrollView, TouchableOpacity, StyleSheet } from 'react-native';

const HomeScreen = () => {
  const [progress, setProgress] = useState(0);
  const [currentDay, setCurrentDay] = useState(0);
  const [totalDays, setTotalDays] = useState(0);

  useEffect(() => {
    // بارگیری داده‌های پیشرفت از دیتابیس
    loadProgressData();
  }, []);

  const loadProgressData = () => {
    // TODO: بارگیری داده‌های پیشرفت
    setCurrentDay(15);
    setTotalDays(30);
    setProgress((15 / 30) * 100);
  };

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>خِتم سوره واقعه</Text>
        <Text style={styles.subtitle}>برنامه‌ی خِتم دوره‌ای</Text>
      </View>

      <View style={styles.progressSection}>
        <Text style={styles.sectionTitle}>پیشرفت خِتم</Text>
        <Text style={styles.progressText}>{Math.round(progress)}%</Text>
        <Text style={styles.progressDetails}>
          {currentDay} روز از {totalDays} روز تمام‌شده
        </Text>
      </View>

      <View style={styles.cardGrid}>
        <TouchableOpacity style={styles.card}>
          <Text style={styles.cardIcon}>📖</Text>
          <Text style={styles.cardTitle}>متن سوره</Text>
          <Text style={styles.cardDesc}>خط عثمان طه</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.card}>
          <Text style={styles.cardIcon}>📅</Text>
          <Text style={styles.cardTitle}>برنامه‌ی خِتم</Text>
          <Text style={styles.cardDesc}>روزانه تیک کنید</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.card}>
          <Text style={styles.cardIcon}>📊</Text>
          <Text style={styles.cardTitle}>آمار پیشرفت</Text>
          <Text style={styles.cardDesc}>نمودار دایره‌ای</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.card}>
          <Text style={styles.cardIcon}>📚</Text>
          <Text style={styles.cardTitle}>تفسیر</Text>
          <Text style={styles.cardDesc}>تفسیر سوره</Text>
        </TouchableOpacity>
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
    justifyContent: 'center',
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#ffffff',
  },
  subtitle: {
    fontSize: 14,
    color: '#bdc3c7',
    marginTop: 5,
  },
  progressSection: {
    backgroundColor: '#ffffff',
    margin: 15,
    padding: 20,
    borderRadius: 10,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 3,
    elevation: 3,
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#1a472a',
    marginBottom: 10,
  },
  progressText: {
    fontSize: 36,
    fontWeight: 'bold',
    color: '#27ae60',
  },
  progressDetails: {
    fontSize: 14,
    color: '#666',
    marginTop: 5,
  },
  cardGrid: {
    flexDirection: 'row-reverse',
    flexWrap: 'wrap',
    paddingHorizontal: 10,
  },
  card: {
    width: '48%',
    backgroundColor: '#ffffff',
    marginHorizontal: 5,
    marginBottom: 15,
    padding: 15,
    borderRadius: 10,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 3,
    elevation: 3,
  },
  cardIcon: {
    fontSize: 32,
    marginBottom: 8,
  },
  cardTitle: {
    fontSize: 14,
    fontWeight: '600',
    color: '#1a472a',
    textAlign: 'center',
  },
  cardDesc: {
    fontSize: 12,
    color: '#95a5a6',
    marginTop: 4,
    textAlign: 'center',
  },
});

export default HomeScreen;