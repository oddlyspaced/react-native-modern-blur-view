import { View, StyleSheet, Text } from 'react-native';
import { ModernBlurViewView } from 'react-native-modern-blur-view';

export default function App() {
  return (
    <View style={styles.container}>
      <Text>ok</Text>
      <ModernBlurViewView
        tintOpacity={0.5}
        enabled
        blurRadius={5}
        tintColor="#32a852"
        style={styles.box}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    position: 'absolute',
    width: 100,
    height: 100,
  },
});
