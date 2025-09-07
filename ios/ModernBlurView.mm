// ModernBlurView.mm

#import "ModernBlurView.h"

#import <react/renderer/components/ModernBlurViewSpec/ComponentDescriptors.h>
#import <react/renderer/components/ModernBlurViewSpec/EventEmitters.h>
#import <react/renderer/components/ModernBlurViewSpec/Props.h>
#import <react/renderer/components/ModernBlurViewSpec/RCTComponentViewHelpers.h>

#import <react/renderer/graphics/Color.h>
// RN provides this symbol at link time; forward declare to avoid brittle headers
extern UIColor *RCTUIColorFromSharedColor(const facebook::react::SharedColor &color);

#import <UIKit/UIKit.h>
#import <objc/runtime.h>

#import "RCTFabricComponentsPlugins.h"

using namespace facebook::react;

//==============================
// MARK: - Private UIBlur hooks
//==============================

@interface UIBlurEffect (Private)
- (id)effectSettings;
@end

//==============================
// MARK: - Defaults / helpers
//==============================

static inline UIBlurEffectStyle const kDefaultBlurStyle = UIBlurEffectStyleLight;
static inline CGFloat const kDefaultBlurRadius = 10.0;

static inline void RNSetSetting(id settings, NSString *key, id value) {
  @try {
    [settings setValue:value forKey:key];
  } @catch (__unused NSException *e) {
    // Ignore keys missing on some iOS versions
  }
}

//==============================
// MARK: - Custom Effect Subclass
//==============================

@interface RNCustomBlurEffect : UIBlurEffect
@property (nonatomic, strong, nullable) NSNumber *blurRadius;
+ (instancetype)effectWithStyle:(UIBlurEffectStyle)style
                     blurRadius:(nullable NSNumber *)radius;
@end

@implementation RNCustomBlurEffect

+ (instancetype)effectWithStyle:(UIBlurEffectStyle)style
                     blurRadius:(NSNumber * _Nullable)radius
{
  id base = [super effectWithStyle:style];
  object_setClass(base, self);
  RNCustomBlurEffect *effect = base;
  effect.blurRadius = radius;
  return effect;
}

- (void)setBlurRadius:(NSNumber * _Nullable)radius {
  objc_setAssociatedObject(self, @selector(blurRadius),
                           radius, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}
- (NSNumber * _Nullable)blurRadius {
  return objc_getAssociatedObject(self, @selector(blurRadius));
}

- (id)effectSettings
{
  id settings = [super effectSettings];

  if (self.blurRadius != nil) {
    RNSetSetting(settings, @"blurRadius", self.blurRadius);
    RNSetSetting(settings, @"blurRadiusSet", @YES);
  }

  RNSetSetting(settings, @"scale", @(UIScreen.mainScreen.scale));
  RNSetSetting(settings, @"grayscaleTintAlpha", @0);
  RNSetSetting(settings, @"luminanceAlpha", @0);
  RNSetSetting(settings, @"colorTintAlpha", @0);
  RNSetSetting(settings, @"colorTint", UIColor.clearColor);
  RNSetSetting(settings, @"tintColor", (id)kCFNull);
  RNSetSetting(settings, @"saturationDeltaFactor", @1.0);

  return settings;
}

- (id)copyWithZone:(NSZone *)zone
{
  id copy = [super copyWithZone:zone];
  object_setClass(copy, [self class]);
  objc_setAssociatedObject(copy, @selector(blurRadius),
                           self.blurRadius, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
  return copy;
}

@end

//==============================
// MARK: - ModernBlurView (Fabric)
//==============================

@interface ModernBlurView () <RCTModernBlurViewViewProtocol>
@end

@implementation ModernBlurView {
  UIView *_container;              // contentView host from base file
  UIVisualEffectView *_blurView;   // actual blur view
  UIView *_tintOverlay;            // tint overlay above blur

  // Cached props
  CGFloat _propBlurRadius;
  UIColor *_propTintColor;
  CGFloat _propTintOpacity;
  BOOL _propEnabled;
  BOOL _propAutoUpdate;
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
  return concreteComponentDescriptorProvider<ModernBlurViewComponentDescriptor>();
}

- (instancetype)initWithFrame:(CGRect)frame
{
  if (self = [super initWithFrame:frame]) {
    static const auto defaultProps =
        std::make_shared<const ModernBlurViewProps>();
    _props = defaultProps;

    // Base class container (preserves create-react-native-library layout pattern)
    _container = [[UIView alloc] initWithFrame:CGRectZero];
    _container.backgroundColor = UIColor.clearColor;

    // Blur + tint setup
    _blurView = [[UIVisualEffectView alloc] initWithEffect:nil];
    _blurView.alpha = 1.0;
    _blurView.backgroundColor = UIColor.clearColor;
    _blurView.contentView.backgroundColor = UIColor.clearColor;

    _tintOverlay = [UIView new];
    _tintOverlay.userInteractionEnabled = NO;
    _tintOverlay.backgroundColor = UIColor.clearColor;

    [_container addSubview:_blurView];
    [_container addSubview:_tintOverlay];

    self.backgroundColor = UIColor.clearColor;
    self.contentView = _container; // <- key line from base

    // Defaults
    _propBlurRadius  = kDefaultBlurRadius;
    _propTintColor   = UIColor.clearColor;
    _propTintOpacity = 1.0;
    _propEnabled     = YES;
    _propAutoUpdate  = NO;

    [self applyEffect];
    [self applyTintOverlay];
  }
  return self;
}

- (void)layoutSubviews
{
  [super layoutSubviews];
  _container.frame = self.bounds;
  _blurView.frame = _container.bounds;
  _tintOverlay.frame = _container.bounds;
  [_container bringSubviewToFront:_tintOverlay];
}

#pragma mark - Trait / Env changes (autoUpdate)

- (void)traitCollectionDidChange:(UITraitCollection *)previousTraitCollection
{
  [super traitCollectionDidChange:previousTraitCollection];
  if (_propAutoUpdate) {
    [self applyEffect];
  }
}

- (void)didMoveToWindow
{
  [super didMoveToWindow];
  if (_propAutoUpdate && self.window) {
    [self applyEffect];
  }
}

#pragma mark - Props glue

- (void)updateProps:(Props::Shared const &)props
          oldProps:(Props::Shared const &)oldProps
{
  const auto &newProps =
      *std::static_pointer_cast<const ModernBlurViewProps>(props);

  // Assign from codegen props (keep names from your *.js/TS spec)
  _propBlurRadius = (CGFloat)newProps.blurRadius;
  _propEnabled    = (BOOL)newProps.enabled;
  _propAutoUpdate = (BOOL)newProps.autoUpdate;

  // SharedColor -> UIColor (nil-safe)
  UIColor *tint = RCTUIColorFromSharedColor(newProps.tintColor);
  _propTintColor = tint ?: UIColor.clearColor;

  _propTintOpacity = (CGFloat)newProps.tintOpacity;

  // Apply immediately (no throttling/animation)
  [self applyEffect];
  [self applyTintOverlay];

  [super updateProps:props oldProps:oldProps];
}

#pragma mark - Effect / Tint application

- (void)applyEffect
{
  if (!_propEnabled) {
    _blurView.effect = nil;
    return;
  }

  UIBlurEffect *newEffect =
      [RNCustomBlurEffect effectWithStyle:kDefaultBlurStyle
                               blurRadius:@(_propBlurRadius)];

  // Clear old effect to avoid cached effect reuse
  _blurView.effect = nil;
  _blurView.effect = newEffect;
}

- (void)applyTintOverlay
{
  UIColor *base = _propTintColor ?: UIColor.clearColor;
  _tintOverlay.backgroundColor = [base colorWithAlphaComponent:_propTintOpacity];
}

#pragma mark - Event emitter / descriptor

- (const ModernBlurViewEventEmitter &)eventEmitter
{
  return static_cast<const ModernBlurViewEventEmitter &>(*_eventEmitter);
}

Class<RCTComponentViewProtocol> ModernBlurViewCls(void)
{
  return ModernBlurView.class;
}

@end
