import { type ColorValue, type ViewProps } from 'react-native';
import type {
  WithDefault,
  Int32,
  Float,
} from 'react-native/Libraries/Types/CodegenTypes';
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';

interface NativeProps extends ViewProps {
  blurRadius?: WithDefault<Int32, 10>;
  tintColor?: ColorValue;
  tintOpacity?: Float;
  enabled?: boolean;
  autoUpdate?: boolean;
}

export default codegenNativeComponent<NativeProps>('ModernBlurViewView');
