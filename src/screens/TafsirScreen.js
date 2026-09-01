import React, { useState, useEffect } from 'react';
import { View, Text, ScrollView, StyleSheet } from 'react-native';

const TafsirScreen = () => {
  const [selectedTafsir, setSelectedTafsir] = useState('nur');
  const [tafsirData, setTafsirData] = useState([]);

  useEffect(() => {
    loadTafsir();
  }, [selectedTafsir]);

  const loadTafsir = () => {
    // TODO: بارگیری تفسیر از دیتابیس یا API
    const sampleTafsir = [
      {
        ayah: 1,
        tafsir: 'این سوره درباره‌ی احوال روز قیامت و سه دسته از انسان‌ها است که در آن روز وجود خواهند داشت.',
        author: 'آیت‌الله ناصر مکارم شیرازی',
      },
      {
        ayah: 2,
        tafsir: 'واقعه به معنی رویدادی است که حتما اتفاق خواهد افتاد و دیگر شکی درباره‌ی آن نخواهد بود.',
        author: 'آیت‌الله ناصر مکارم شیرازی',
      },
    ];
    setTafsirData(sampleTafsir);
  };

  const tafsirs = [
    { id: 'nur', name: 'تفسیر نور', author: 'آیت‌الله ناصر مکارم شیرازی' },
    { id: 'qarati', name: 'تفسیر قرائتی', author: 'حاج سیّد عبدالعظیم قرائتی' },
  ];

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>تفسیر سوره واقعه</Text>
      </View>

      <View style={styles.tafsirSelector}>
        <Text style={styles.sectionTitle}>انتخاب تفسیر</Text>
        {tafsirs.map(tf => (
          <View
            key={tf.id}
            style={[
              styles.tafsirOption,
              selectedTafsir === tf.id && styles.tafsirOptionActive,
            ]}
          >
            <Text style={styles.tafsirName}>{tf.name}</Text>
            <Text style={styles.tafsirAuthor}>{tf.author}</Text>
          </View>
        ))}
      </View>

      <View style={styles.contentSection}>
        <Text style={styles.contentTitle}>متن تفسیر</Text>
        {tafsirData.map((item, index) => (
          <View key={index} style={styles.tafsirBox}>
            <Text style={styles.ayahNum}>آیه {item.ayah}</Text>
            <Text style={styles.tafsirContent}>{item.tafsir}</Text>
            <Text style={styles.tafsirAuthor2}>{item.author}</Text>
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
  tafsirSelector: {
    backgroundColor: '#ffffff',
    margin: 15,
    padding: 15,
    borderRadius: 8,
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#1a472a',
    marginBottom: 12,
    textAlign: 'right',
  },
  tafsirOption: {
    backgroundColor: '#f9f9f9',
    padding: 12,
    marginBottom: 8,
    borderRadius: 6,
    borderLeftWidth: 3,
    borderLeftColor: '#bdc3c7',
  },
  tafsirOptionActive: {
    backgroundColor: '#e8f5e9',
    borderLeftColor: '#27ae60',
  },
  tafsirName: {
    fontSize: 14,
    fontWeight: '600',
    color: '#1a472a',
    textAlign: 'right',
  },
  tafsirAuthor: {
    fontSize: 12,
    color: '#95a5a6',
    marginTop: 4,
    textAlign: 'right',
  },
  contentSection: {
    paddingHorizontal: 15,
    paddingBottom: 20,
  },
  contentTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#1a472a',
    marginBottom: 12,
    textAlign: 'right',
  },
  tafsirBox: {
    backgroundColor: '#ffffff',
    padding: 15,
    marginBottom: 10,
    borderRadius: 8,
    borderRightWidth: 4,
    borderRightColor: '#27ae60',
  },
  ayahNum: {
    fontSize: 12,
    fontWeight: '600',
    color: '#27ae60',
    marginBottom: 8,
    textAlign: 'right',
  },
  tafsirContent: {
    fontSize: 14,
    lineHeight: 24,
    color: '#555',
    textAlign: 'right',
    marginBottom: 10,
  },
  tafsirAuthor2: {
    fontSize: 12,
    color: '#95a5a6',
    textAlign: 'right',
  },
});

export default TafsirScreen;