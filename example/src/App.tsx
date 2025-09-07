import { View, StyleSheet, Text } from 'react-native';
import { BlurView } from 'react-native-modern-blur-view';
// import { ModernBlurViewView } from 'react-native-modern-blur-view';
// import { BlurView } from '../../src/BlurView';

export default function App() {
  return (
    <View style={styles.container}>
      <Text>ok</Text>
      <BlurView
        tintOpacity={0.5}
        enabled
        blurRadius={4}
        tintColor="#32a852"
        style={styles.box}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: 'white',
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    position: 'absolute',
    width: 100,
    height: 100,
  },
});
