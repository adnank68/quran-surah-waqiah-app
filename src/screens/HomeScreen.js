import React, { useState, useEffect } from 'react';
import { View, Text, ScrollView, TouchableOpacity, StyleSheet } from 'react-native';
import { PieChart } from 'react-native-chart-kit';

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

  const chartData = {
    labels: ['تمام‌شده', 'باقی‌مانده'],
    datasets: [
      {
        data: [progress, 100 - progress],
      },
    ],
    colors: ['#4CAF50', '#E0E0E0'],
  };

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>ختم سوره واقعه</Text>
        <Text style={styles.subtitle}>برنامه‌ی ختم دوره‌ای</Text>
      </View>

      <View style={styles.progressSection}>
        <Text style={styles.sectionTitle}>پیشرفت ختم</Text>
        <Text style={styles.progressText}>{Math.round(progress)}%</Text>
        <Text style={styles.progressDetails}>
          {currentDay} روز از {totalDays} روز
        </Text>
      </View>

      <View style={styles.chartSection}>
        <PieChart
          data={chartData}
          width={300}
          height={300}
          chartConfig={{
            backgroundColor: '#ffffff',
            color: (opacity = 1) => `rgba(0, 0, 0, ${opacity})`,
          }}
        />
      </View>

      <TouchableOpacity style={styles.button}>
        <Text style={styles.buttonText}>نمایش برنامه‌ی ختم</Text>
      </TouchableOpacity>

      <TouchableOpacity style={styles.button}>
        <Text style={styles.buttonText}>نمایش سوره</Text>
      </TouchableOpacity>

      <TouchableOpacity style={styles.button}>
        <Text style={styles.buttonText}>تفسیر سوره</Text>
      </TouchableOpacity>
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
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#2c3e50',
    marginBottom: 10,
  },
  progressText: {
    fontSize: 36,
    fontWeight: 'bold',
    color: '#4CAF50',
  },
  progressDetails: {
    fontSize: 14,
    color: '#666',
    marginTop: 5,
  },
  chartSection: {
    alignItems: 'center',
    backgroundColor: '#ffffff',
    margin: 15,
    borderRadius: 10,
    padding: 10,
  },
  button: {
    backgroundColor: '#3498db',
    marginHorizontal: 15,
    marginVertical: 8,
    paddingVertical: 15,
    borderRadius: 8,
    alignItems: 'center',
  },
  buttonText: {
    color: '#ffffff',
    fontSize: 16,
    fontWeight: '600',
  },
});

export default HomeScreen;
