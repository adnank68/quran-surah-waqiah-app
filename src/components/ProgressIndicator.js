import React from 'react';
import { View, Text, StyleSheet } from 'react-native';

const ProgressIndicator = ({ progress, size = 200 }) => {
  const radius = size / 2;
  const circumference = 2 * Math.PI * (radius - 10);
  const strokeDashoffset = circumference - (progress / 100) * circumference;

  return (
    <View style={[styles.container, { width: size, height: size }]}>
      <svg width={size} height={size}>
        {/* Background circle */}
        <circle
          cx={radius}
          cy={radius}
          r={radius - 10}
          fill="none"
          stroke="#ecf0f1"
          strokeWidth="8"
        />
        {/* Progress circle */}
        <circle
          cx={radius}
          cy={radius}
          r={radius - 10}
          fill="none"
          stroke="#27ae60"
          strokeWidth="8"
          strokeDasharray={circumference}
          strokeDashoffset={strokeDashoffset}
          strokeLinecap="round"
          transform={`rotate(-90 ${radius} ${radius})`}
        />
      </svg>
      <View style={styles.textContainer}>
        <Text style={styles.percentageText}>{Math.round(progress)}%</Text>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    justifyContent: 'center',
    alignItems: 'center',
  },
  textContainer: {
    position: 'absolute',
    justifyContent: 'center',
    alignItems: 'center',
  },
  percentageText: {
    fontSize: 36,
    fontWeight: 'bold',
    color: '#27ae60',
  },
});

export default ProgressIndicator;
