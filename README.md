# react-native-modern-blur-view

A lightweight, Fabric-ready BlurView for React Native that blurs whatever is behind it — with precise control over **blur radius**, **tint color**, and **tint opacity**. Works on iOS & Android (Fabric/New Architecture).

> ✨ Includes a demo app with a draggable blur square and adjustable controls.

---

## Features

* 🧊 True background blur, not a screenshot hack
* 🎛️ Props for `blurRadius`, `tintColor`, `tintOpacity`, `enabled`
* 🧱 Drop-in `<View/>` replacement (accepts all `ViewProps`)
* ⚙️ Compatible with New Architecture

---

## Installation

```bash
# with npm
npm i react-native-modern-blur-view

# with yarn
yarn add react-native-modern-blur-view
```

### iOS (New Architecture)

```bash
cd ios
RCT_NEW_ARCH_ENABLED=1 pod install
cd ..
```

* Xcode ≥ 15 recommended.

### Android

* No manual steps required.
* Tested on Android 12+. (Older versions may work, but are not tested.)

---

## Quick Start

```tsx
import { View, StyleSheet } from 'react-native';
import { BlurView } from 'react-native-modern-blur-view';

export default function Example() {
  return (
    <View style={styles.container}>
      {/* Content behind the blur goes here */}

      <BlurView
        enabled
        blurRadius={16}        // 0..25 on Android, > 25 on iOS
        tintColor="#2323FF"    // any CSS color
        tintOpacity={0.35}     // 0..1
        style={styles.blurBox}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1 },
  blurBox: {
    position: 'absolute',
    right: 24,
    bottom: 24,
    width: 160,
    height: 160,
    borderRadius: 16,
  },
});
```

---

## Props

The component extends `ViewProps`.

| Prop          | Type                   | Default     | Platform    | Description                                                                      |
| ------------- | ---------------------- | ----------- | ----------- | -------------------------------------------------------------------------------- |
| `blurRadius`  | `number`               | `10`        | iOS/Android | Strength of blur, **0–25** (Android scale, > 25 values are supported on iOS). Use higher values for heavier blur. |
| `tintColor`   | `string`               | `undefined` | iOS/Android | CSS color string (`#RRGGBB`, etc.). Applied over the blur.          |
| `tintOpacity` | `number`               | `0`         | iOS/Android | 0–1 opacity of the tint overlay.                                                 |
| `enabled`     | `boolean`              | `true`      | iOS/Android | Toggles the effect without unmounting the view.                                  |
| `style`       | `StyleProp<ViewStyle>` | —           | iOS/Android | Size/position/border radius, like any `<View/>`.                                 |

> ℹ️ The view **blurs content behind it**, so you’ll usually want it properly positioned over existing content.

---

## Demo App (Draggable BlurView)

This repo ships with an example showcasing:

* Scrollable content (images + text)
* A **draggable** floating BlurView (no extra gesture libs)
* Bottom control panel with **+ / –** buttons for size, opacity, radius

**Run it locally:**

```bash
# in the repo root
yarn
yarn prepare
cd example
yarn

# iOS
yarn ios

# Android (device/emulator running)
yarn android
```

---

## Tips & Best Practices

* **Visual parity with design tools:**
  The `blurRadius` passes the raw value to underlying views. On Android 25 is the max value, on iOS it goes way beyond.

* **Performance:**
  Keep the blur area reasonably sized. On older devices, very large blurs can be expensive.

---

## API Surface (TypeScript)

```ts
import { ViewProps } from 'react-native';

export type BlurViewProps = ViewProps & {
  blurRadius?: number;   // float
  tintColor?: string;    // '#2323FF', etc.
  tintOpacity?: number;  // 0..1
  enabled?: boolean;     // default: true
};
```

---

## Have a feature request? Open an issue!

## Contributing

PRs welcome! Please:

1. Branch from `main`
2. Keep changes minimal and well-scoped
3. Add/adjust example if you change behavior
4. Run example on both iOS & Android before opening your PR

## Credits

- Hardik Srivastava (@oddlyspaced)


## References
- https://github.com/Kureev/react-native-blur
- https://github.com/efremidze/VisualEffectView