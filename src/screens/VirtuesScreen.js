import React, { useState, useEffect } from 'react';
import { View, Text, ScrollView, StyleSheet } from 'react-native';

const VirtuesScreen = () => {
  const [virtues, setVirtues] = useState([]);

  useEffect(() => {
    loadVirtues();
  }, []);

  const loadVirtues = () => {
    const virtuesList = [
      {
        id: 1,
        title: 'فضیلت سوره واقعه',
        hadith:
          'پیامبر (ص) فرمودند: "کسی که سوره واقعه را بخواند، هیچ‌گاه گرسنگی از او نخواهد رفت."',
        source: 'روایات معتبر',
      },
      {
        id: 2,
        title: 'برکت سوره واقعه',
        hadith:
          'امام صادق (ع) فرمودند: "برای سوره واقعه فضایل زیاد است و کسی که آن را بخواند خیر و برکت فراوانی دارد."',
        source: 'کتاب‌های حدیثی',
      },
      {
        id: 3,
        title: 'شان نزول سوره',
        hadith:
          'این سوره در مدینه نازل شد و درباره احوال روز قیامت و سه دسته از انسان‌ها در آن روز است.',
        source: 'تفاسیر معتبر',
      },
      {
        id: 4,
        title: 'معنای واقعه',
        hadith:
          'واقعه به معنی رویدادی است که حتماً اتفاق خواهد افتاد و دیگر شک‌ی درباره آن نخواهد بود.',
        source: 'لغت‌نامه‌های قرآنی',
      },
      {
        id: 5,
        title: 'محتوای سوره',
        hadith:
          'سوره واقعه به توصیف روز قیامت، جمع آوری انسان‌ها، سه دسته از مردم (اقتربون، اصحاب میمنه، اصحاب مشئمه) و بیان انجام‌شان می‌پردازد.',
        source: 'تفسیرهای معتبر',
      },
    ];
    setVirtues(virtuesList);
  };

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>فضایل و شان نزول</Text>
        <Text style={styles.subtitle}>سوره واقعه</Text>
      </View>

      <View style={styles.contentContainer}>
        {virtues.map(virtue => (
          <View key={virtue.id} style={styles.virtueCard}>
            <View style={styles.titleContainer}>
              <Text style={styles.virtueTitle}>{virtue.title}</Text>
            </View>
            <Text style={styles.hadith}>{virtue.hadith}</Text>
            <Text style={styles.source}>📚 {virtue.source}</Text>
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
    fontSize: 24,
    fontWeight: 'bold',
    color: '#ffffff',
  },
  subtitle: {
    fontSize: 14,
    color: '#bdc3c7',
    marginTop: 8,
  },
  contentContainer: {
    padding: 15,
  },
  virtueCard: {
    backgroundColor: '#ffffff',
    borderRadius: 8,
    padding: 15,
    marginBottom: 12,
    borderRightWidth: 4,
    borderRightColor: '#27ae60',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 3,
    elevation: 3,
  },
  titleContainer: {
    marginBottom: 12,
  },
  virtueTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#27ae60',
    textAlign: 'right',
  },
  hadith: {
    fontSize: 14,
    lineHeight: 22,
    color: '#555',
    textAlign: 'right',
    marginBottom: 10,
    fontStyle: 'italic',
  },
  source: {
    fontSize: 12,
    color: '#95a5a6',
    textAlign: 'right',
  },
});

export default VirtuesScreen;