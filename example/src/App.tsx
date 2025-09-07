// App.tsx
import React, { useMemo, useRef, useState, useCallback } from 'react';
import {
  View,
  Text,
  Image,
  StyleSheet,
  ScrollView,
  SafeAreaView,
  Dimensions,
  PanResponder,
  Animated,
  TouchableOpacity,
  type LayoutChangeEvent,
} from 'react-native';
import { BlurView } from 'react-native-modern-blur-view';

const { width: SCREEN_WIDTH, height: SCREEN_HEIGHT } = Dimensions.get('window');

export default function App() {
  const [size, setSize] = useState(160);
  const [opacity, setOpacity] = useState(0.5);
  const [radius, setRadius] = useState(12);

  const [bounds, setBounds] = useState({ w: SCREEN_WIDTH, h: SCREEN_HEIGHT });
  const onContentLayout = useCallback((e: LayoutChangeEvent) => {
    const { width, height } = e.nativeEvent.layout;
    setBounds({ w: width, h: height });
  }, []);

  const translate = useRef(
    new Animated.ValueXY({
      x: (SCREEN_WIDTH - 160) / 2,
      y: SCREEN_HEIGHT * 0.28,
    })
  ).current;
  const lastOffset = useRef({
    x: (SCREEN_WIDTH - 160) / 2,
    y: SCREEN_HEIGHT * 0.28,
  });

  const clamp = (val: number, min: number, max: number) =>
    Math.min(Math.max(val, min), max);

  const panResponder = useRef(
    PanResponder.create({
      onStartShouldSetPanResponder: () => true,
      onPanResponderGrant: () => {
        translate.setOffset(lastOffset.current);
        translate.setValue({ x: 0, y: 0 });
      },
      onPanResponderMove: (_, gesture) => {
        translate.setValue({ x: gesture.dx, y: gesture.dy });
      },
      onPanResponderRelease: (_, gesture) => {
        translate.flattenOffset();
        const maxX = Math.max(0, bounds.w - size);
        const maxY = Math.max(0, bounds.h - size);
        const nextX = clamp(lastOffset.current.x + gesture.dx, 0, maxX);
        const nextY = clamp(lastOffset.current.y + gesture.dy, 0, maxY);
        lastOffset.current = { x: nextX, y: nextY };
        translate.setValue({ x: nextX, y: nextY });
      },
    })
  ).current;

  const floatingSizeStyle = useMemo(
    () => [{ width: size, height: size }],
    [size]
  );

  const ControlRow = ({
    label,
    value,
    onInc,
    onDec,
  }: {
    label: string;
    value: string;
    onInc: () => void;
    onDec: () => void;
  }) => (
    <View style={styles.controlRow}>
      <Text style={styles.controlLabel}>
        {label}: {value}
      </Text>
      <View style={styles.buttonRow}>
        <TouchableOpacity style={styles.btn} onPress={onDec}>
          <Text style={styles.btnText}>-</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.btn} onPress={onInc}>
          <Text style={styles.btnText}>+</Text>
        </TouchableOpacity>
      </View>
    </View>
  );

  return (
    <SafeAreaView style={styles.safe}>
      <View style={styles.container}>
        <View style={styles.contentArea} onLayout={onContentLayout}>
          <ScrollView
            contentContainerStyle={styles.scrollContent}
            style={styles.scroll}
          >
            <Text style={styles.h1}>BlurView Demo (Draggable + Buttons)</Text>
            <Text style={styles.p}>
              Drag the green blurred square around. Adjust its size, opacity,
              and blur radius using the + / – buttons below.
            </Text>

            {Array.from({ length: 3 }).map((_, row) => (
              <View key={row} style={styles.row}>
                {Array.from({ length: 3 }).map((__, col) => (
                  <Image
                    key={`${row}-${col}`}
                    source={{
                      uri: `https://picsum.photos/seed/${row}-${col}/${
                        Math.round(SCREEN_WIDTH / 3) + 30
                      }/180`,
                    }}
                    style={styles.cardImg}
                    resizeMode="cover"
                  />
                ))}
              </View>
            ))}

            <Text style={styles.h2}>Some filler text</Text>
            {Array.from({ length: 6 }).map((_, i) => (
              <Text key={i} style={styles.p}>
                Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer
                blandit sapien in varius tincidunt.
              </Text>
            ))}
          </ScrollView>

          <Animated.View
            style={[
              styles.floating,
              floatingSizeStyle,
              {
                transform: [
                  { translateX: translate.x },
                  { translateY: translate.y },
                ],
              },
            ]}
            {...panResponder.panHandlers}
          >
            <BlurView
              enabled
              blurRadius={radius}
              tintOpacity={opacity}
              tintColor="#32a852"
              style={StyleSheet.absoluteFill}
            />
            <Text style={styles.floatingLabel}>
              {Math.round(size)}px • {opacity.toFixed(2)} • r
              {Math.round(radius)}
            </Text>
          </Animated.View>
        </View>

        {/* Controls with + / – */}
        <View style={styles.controls}>
          <ControlRow
            label="Size"
            value={`${Math.round(size)}px`}
            onInc={() => setSize((s) => Math.min(s + 10, 320))}
            onDec={() => setSize((s) => Math.max(s - 10, 80))}
          />
          <ControlRow
            label="Opacity"
            value={opacity.toFixed(2)}
            onInc={() => setOpacity((o) => Math.min(o + 0.05, 1))}
            onDec={() => setOpacity((o) => Math.max(o - 0.05, 0))}
          />
          <ControlRow
            label="Radius"
            value={`${Math.round(radius)}`}
            onInc={() => setRadius((r) => Math.min(r + 2, 100))}
            onDec={() => setRadius((r) => Math.max(r - 2, 0))}
          />
        </View>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safe: { flex: 1, backgroundColor: '#fff' },
  container: { flex: 1, backgroundColor: '#fff' },
  contentArea: { flex: 1 },
  scroll: { flex: 1 },
  scrollContent: {
    paddingTop: 12,
    paddingBottom: 160,
    paddingHorizontal: 16,
  },
  h1: { fontSize: 24, fontWeight: '800', marginBottom: 8 },
  h2: { fontSize: 18, fontWeight: '700', marginTop: 16, marginBottom: 8 },
  p: { fontSize: 15, lineHeight: 21, color: '#222', marginBottom: 10 },
  row: { flexDirection: 'row', gap: 8, marginVertical: 6 },
  cardImg: { flex: 1, height: 120, borderRadius: 10 },

  floating: {
    position: 'absolute',
    borderRadius: 16,
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: 'rgba(0,0,0,0.25)',
    overflow: 'hidden',
  },
  floatingLabel: {
    position: 'absolute',
    bottom: 6,
    left: 8,
    right: 8,
    fontSize: 12,
    color: '#0c0c0c',
    backgroundColor: 'rgba(255,255,255,0.75)',
    paddingHorizontal: 6,
    paddingVertical: 2,
    borderRadius: 6,
    textAlign: 'center',
  },

  controls: {
    position: 'absolute',
    left: 0,
    right: 0,
    bottom: 0,
    paddingHorizontal: 16,
    paddingVertical: 12,
    backgroundColor: 'rgba(255,255,255,0.96)',
    borderTopWidth: StyleSheet.hairlineWidth,
    borderTopColor: '#ddd',
    gap: 10,
  },
  controlRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  controlLabel: { fontSize: 14, fontWeight: '600' },
  buttonRow: { flexDirection: 'row', gap: 12 },
  btn: {
    width: 36,
    height: 36,
    borderRadius: 18,
    backgroundColor: '#32a852',
    justifyContent: 'center',
    alignItems: 'center',
  },
  btnText: { color: 'white', fontSize: 20, fontWeight: '700' },
});
