import React, { useState, useEffect } from 'react';
import { View, Text, ScrollView, TouchableOpacity, StyleSheet } from 'react-native';

const QuranScreen = () => {
  const [selectedTranslations, setSelectedTranslations] = useState(['ansarian']);
  const [currentAyah, setCurrentAyah] = useState(0);
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
    const sampleAyahs = Array.from({ length: 96 }, (_, i) => ({
      id: i,
      number: i + 1,
      text: 'بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ',
      translations: {
        ansarian: 'به نام خدای بخشنده‌ی مهربان',
        elahi_ghomshei: 'به نام خدای رحمان و رحیم',
        qorrati: 'به نام پروردگاری که بسیار رحمت‌کار است',
        makarem: 'به نام خدای بخشندگی‌کننده و مهربان',
      },
    }));
    setAyahs(sampleAyahs);
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
        <Text style={styles.surahInfo}>۹۶ آیت • سوره ۵۶</Text>
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
        {ayahs.slice(0, 5).map(ayah => (
          <View key={ayah.id} style={styles.ayahBox}>
            <Text style={styles.ayahText}>{ayah.text}</Text>
            <Text style={styles.ayahNumber}>({ayah.number})</Text>
            
            {selectedTranslations.map(transId => (
              <View key={transId} style={styles.translationBox}>
                <Text style={styles.translationLabel}>
                  {translations.find(t => t.id === transId)?.name}:
                </Text>
                <Text style={styles.translationText}>
                  {ayah.translations[transId]}
                </Text>
              </View>
            ))}
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
    backgroundColor: '#1a472a',
    padding: 20,
    alignItems: 'center',
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#ffffff',
  },
  surahInfo: {
    fontSize: 14,
    color: '#bdc3c7',
    marginTop: 5,
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
    color: '#1a472a',
    marginBottom: 10,
    textAlign: 'right',
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
    backgroundColor: '#27ae60',
    borderColor: '#229954',
  },
  transButtonText: {
    color: '#1a472a',
    fontSize: 12,
  },
  transButtonTextActive: {
    color: '#ffffff',
  },
  ayahsSection: {
    paddingHorizontal: 15,
    paddingBottom: 20,
  },
  ayahBox: {
    backgroundColor: '#ffffff',
    padding: 15,
    marginBottom: 10,
    borderRadius: 8,
    borderRightWidth: 4,
    borderRightColor: '#27ae60',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 3,
    elevation: 3,
  },
  ayahText: {
    fontSize: 20,
    lineHeight: 36,
    color: '#1a472a',
    textAlign: 'right',
  },
  ayahNumber: {
    fontSize: 12,
    color: '#95a5a6',
    textAlign: 'center',
    marginVertical: 8,
  },
  translationBox: {
    marginTop: 12,
    paddingTop: 12,
    borderTopWidth: 1,
    borderTopColor: '#ecf0f1',
  },
  translationLabel: {
    fontSize: 12,
    fontWeight: '600',
    color: '#27ae60',
    marginBottom: 5,
    textAlign: 'right',
  },
  translationText: {
    fontSize: 14,
    lineHeight: 24,
    color: '#555',
    textAlign: 'right',
  },
});

export default QuranScreen;