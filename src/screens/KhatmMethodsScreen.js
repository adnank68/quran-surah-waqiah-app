import React, { useState, useEffect } from 'react';
import { View, Text, ScrollView, TouchableOpacity, StyleSheet } from 'react-native';

const KhatmMethodsScreen = () => {
  const [methods, setMethods] = useState([]);
  const [expandedMethod, setExpandedMethod] = useState(null);

  useEffect(() => {
    loadMethods();
  }, []);

  const loadMethods = () => {
    const khatmMethods = [
      {
        id: 1,
        title: 'روش ختم از مفاتیح الجنان',
        source: 'مفاتیح الجنان',
        description: 'طریقه ختم سوره واقعه به روش مفاتیح الجنان که یکی از قدیمی‌ترین منابع معتبر برای ختم قرآن است.',
        steps: [
          'نیت کنید برای خدا و به نیت ختم سوره واقعه',
          'سوره واقعه را با تدبر و خشوع بخوانید',
          'بعد از خواندن دعای مختصی را بخوانید',
          'برای خیر خود و دیگران دعا کنید',
        ],
      },
      {
        id: 2,
        title: 'روش ختم آیت‌الله بهجت',
        source: 'آیت‌الله بهجت',
        description: 'روش مجرب و مورد تأیید آیت‌الله محمد تقی بهجت برای ختم سوره واقعه.',
        steps: [
          'غسل انجام دهید یا وضو بگیرید',
          'سوره واقعه را به احترام بخوانید',
          'دعای ختم را تمام کنید',
          'صدقه و خیرات انجام دهید',
        ],
      },
      {
        id: 3,
        title: 'روش ختم حاج آقا عالی',
        source: 'حاج آقا عالی',
        description: 'روش مشهور و پرفضیل حاج آقا عالی برای ختم سوره واقعه.',
        steps: [
          'با نیت خالص شروع کنید',
          'سوره واقعه را آهسته و با توجه بخوانید',
          'دعای توسل بخوانید',
          'نتیجه ختم را برای مومنین بخشش کنید',
        ],
      },
    ];
    setMethods(khatmMethods);
  };

  const MethodCard = ({ method, isExpanded, onToggle }) => (
    <TouchableOpacity
      style={styles.methodCard}
      onPress={onToggle}
      activeOpacity={0.7}
    >
      <View style={styles.methodHeader}>
        <Text style={styles.methodTitle}>{method.title}</Text>
        <Text style={styles.expandIcon}>{isExpanded ? '▼' : '▶'}</Text>
      </View>

      <Text style={styles.methodSource}>{method.source}</Text>

      {isExpanded && (
        <View style={styles.methodContent}>
          <Text style={styles.description}>{method.description}</Text>

          <Text style={styles.stepsTitle}>مراحل</Text>
          {method.steps.map((step, index) => (
            <View key={index} style={styles.stepItem}>
              <Text style={styles.stepNumber}>{index + 1}.</Text>
              <Text style={styles.stepText}>{step}</Text>
            </View>
          ))}
        </View>
      )}
    </TouchableOpacity>
  );

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>روش‌های ختم معتبر</Text>
        <Text style={styles.subtitle}>از منابع و علما معتبر</Text>
      </View>

      <View style={styles.methodsContainer}>
        {methods.map(method => (
          <MethodCard
            key={method.id}
            method={method}
            isExpanded={expandedMethod === method.id}
            onToggle={() =>
              setExpandedMethod(
                expandedMethod === method.id ? null : method.id
              )
            }
          />
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
  methodsContainer: {
    padding: 15,
  },
  methodCard: {
    backgroundColor: '#ffffff',
    borderRadius: 8,
    marginBottom: 12,
    overflow: 'hidden',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 3,
    elevation: 3,
  },
  methodHeader: {
    flexDirection: 'row-reverse',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: 15,
    backgroundColor: '#f9f9f9',
  },
  methodTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#1a472a',
    flex: 1,
    textAlign: 'right',
  },
  expandIcon: {
    fontSize: 12,
    color: '#27ae60',
    marginRight: 10,
  },
  methodSource: {
    fontSize: 12,
    color: '#95a5a6',
    paddingHorizontal: 15,
    paddingTop: 8,
    paddingBottom: 8,
    textAlign: 'right',
  },
  methodContent: {
    padding: 15,
    borderTopWidth: 1,
    borderTopColor: '#ecf0f1',
  },
  description: {
    fontSize: 14,
    lineHeight: 22,
    color: '#555',
    textAlign: 'right',
    marginBottom: 15,
  },
  stepsTitle: {
    fontSize: 14,
    fontWeight: '600',
    color: '#1a472a',
    marginBottom: 10,
    textAlign: 'right',
  },
  stepItem: {
    flexDirection: 'row-reverse',
    marginBottom: 10,
    paddingRight: 10,
  },
  stepNumber: {
    fontSize: 14,
    fontWeight: '600',
    color: '#27ae60',
    marginLeft: 10,
  },
  stepText: {
    fontSize: 14,
    lineHeight: 20,
    color: '#555',
    flex: 1,
    textAlign: 'right',
  },
});

export default KhatmMethodsScreen;