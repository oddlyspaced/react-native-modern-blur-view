import { Platform, StyleSheet, View, type ViewProps } from 'react-native';
import ModernBlurViewNativeComponent from './ModernBlurViewNativeComponent';

export type IModernBlurViewProps = ViewProps & {
  blurRadius?: number;
  tintColor?: string;
  tintOpacity?: number;
  enabled?: boolean;
};

const NativeComponent =
  Platform.OS === 'android' || Platform.OS === 'ios'
    ? ModernBlurViewNativeComponent
    : View;

const BlurView = (props: IModernBlurViewProps) => {
  const {
    blurRadius = 10,
    enabled = true,
    tintColor,
    tintOpacity = 0,
    style,
    children,
    ...rest
  } = props;

  return (
    <View {...rest} style={StyleSheet.compose({ overflow: 'hidden' }, style)}>
      <NativeComponent
        blurRadius={blurRadius}
        enabled={enabled && blurRadius > 0}
        pointerEvents="none"
        tintColor={tintColor}
        tintOpacity={tintOpacity}
        style={{
          backgroundColor: 'transparent',
          ...StyleSheet.absoluteFillObject,
        }}
      >
        {children}
      </NativeComponent>
    </View>
  );
};

export default BlurView;
