import React, { useState, useEffect } from 'react';
import { View, Text, ScrollView, TouchableOpacity, StyleSheet } from 'react-native';

const QuranScreen = () => {
  const [selectedTranslations, setSelectedTranslations] = useState([]);
  const [ayahs, setAyahs] = useState([]);

  const translations = [
    { id: 'ansarian', name: 'شیخ انصاریان' },
    { id: 'elahi_ghomshei', name: 'الهی قمشه‌ای' },
    { id: 'qorrati', name: 'قرائتی' },
    { id: 'makarem', name: 'مکارم شیرازی' },
  ];

  useEffect(() => {
    loadAyahs();
  }, []);

  const loadAyahs = () => {
    // TODO: بارگیری ایات از دیتابیس یا API
    setAyahs([
      {
        id: 1,
        number: 1,
        text: 'بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ',
      },
    ]);
  };

  const toggleTranslation = (translationId) => {
    if (selectedTranslations.includes(translationId)) {
      setSelectedTranslations(
        selectedTranslations.filter(t => t !== translationId)
      );
    } else {
      setSelectedTranslations([...selectedTranslations, translationId]);
    }
  };

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>سوره واقعه</Text>
      </View>

      <View style={styles.translationSelector}>
        <Text style={styles.sectionTitle}>انتخاب ترجمه‌ها</Text>
        <View style={styles.translationButtons}>
          {translations.map(trans => (
            <TouchableOpacity
              key={trans.id}
              style={[
                styles.transButton,
                selectedTranslations.includes(trans.id) &&
                  styles.transButtonActive,
              ]}
              onPress={() => toggleTranslation(trans.id)}
            >
              <Text
                style={[
                  styles.transButtonText,
                  selectedTranslations.includes(trans.id) &&
                    styles.transButtonTextActive,
                ]}
              >
                {trans.name}
              </Text>
            </TouchableOpacity>
          ))}
        </View>
      </View>

      <View style={styles.ayahsSection}>
        {ayahs.map(ayah => (
          <View key={ayah.id} style={styles.ayahBox}>
            <Text style={styles.ayahText}>{ayah.text}</Text>
            <Text style={styles.ayahNumber}>({ayah.number})</Text>
            {/* ترجمه‌های انتخاب‌شده */}
          </View>
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
    fontSize: 28,
    fontWeight: 'bold',
    color: '#ffffff',
  },
  translationSelector: {
    backgroundColor: '#ffffff',
    margin: 15,
    padding: 15,
    borderRadius: 8,
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#2c3e50',
    marginBottom: 10,
  },
  translationButtons: {
    flexDirection: 'row-reverse',
    flexWrap: 'wrap',
  },
  transButton: {
    backgroundColor: '#ecf0f1',
    padding: 8,
    margin: 5,
    borderRadius: 20,
    borderWidth: 1,
    borderColor: '#bdc3c7',
  },
  transButtonActive: {
    backgroundColor: '#3498db',
    borderColor: '#2980b9',
  },
  transButtonText: {
    color: '#2c3e50',
    fontSize: 12,
  },
  transButtonTextActive: {
    color: '#ffffff',
  },
  ayahsSection: {
    padding: 15,
  },
  ayahBox: {
    backgroundColor: '#ffffff',
    padding: 15,
    marginBottom: 10,
    borderRadius: 8,
    borderRightWidth: 3,
    borderRightColor: '#3498db',
  },
  ayahText: {
    fontSize: 18,
    lineHeight: 32,
    color: '#2c3e50',
    textAlign: 'right',
    fontFamily: 'OthamanTaha',
  },
  ayahNumber: {
    fontSize: 12,
    color: '#95a5a6',
    textAlign: 'center',
    marginTop: 10,
  },
});

export default QuranScreen;
