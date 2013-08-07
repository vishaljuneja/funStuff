define(["jquery"],function(t){t.easing.jswing=t.easing.swing,t.extend(t.easing,{def:"easeOutQuad",swing:function(e,n,i,r,o){return t.easing[t.easing.def](e,n,i,r,o)},easeInQuad:function(t,e,n,i,r){return i*(e/=r)*e+n},easeOutQuad:function(t,e,n,i,r){return-i*(e/=r)*(e-2)+n},easeInOutQuad:function(t,e,n,i,r){return 1>(e/=r/2)?i/2*e*e+n:-i/2*(--e*(e-2)-1)+n},easeInCubic:function(t,e,n,i,r){return i*(e/=r)*e*e+n},easeOutCubic:function(t,e,n,i,r){return i*((e=e/r-1)*e*e+1)+n},easeInOutCubic:function(t,e,n,i,r){return 1>(e/=r/2)?i/2*e*e*e+n:i/2*((e-=2)*e*e+2)+n},easeInQuart:function(t,e,n,i,r){return i*(e/=r)*e*e*e+n},easeOutQuart:function(t,e,n,i,r){return-i*((e=e/r-1)*e*e*e-1)+n},easeInOutQuart:function(t,e,n,i,r){return 1>(e/=r/2)?i/2*e*e*e*e+n:-i/2*((e-=2)*e*e*e-2)+n},easeInQuint:function(t,e,n,i,r){return i*(e/=r)*e*e*e*e+n},easeOutQuint:function(t,e,n,i,r){return i*((e=e/r-1)*e*e*e*e+1)+n},easeInOutQuint:function(t,e,n,i,r){return 1>(e/=r/2)?i/2*e*e*e*e*e+n:i/2*((e-=2)*e*e*e*e+2)+n},easeInSine:function(t,e,n,i,r){return-i*Math.cos(e/r*(Math.PI/2))+i+n},easeOutSine:function(t,e,n,i,r){return i*Math.sin(e/r*(Math.PI/2))+n},easeInOutSine:function(t,e,n,i,r){return-i/2*(Math.cos(Math.PI*e/r)-1)+n},easeInExpo:function(t,e,n,i,r){return 0==e?n:i*Math.pow(2,10*(e/r-1))+n},easeOutExpo:function(t,e,n,i,r){return e==r?n+i:i*(-Math.pow(2,-10*e/r)+1)+n},easeInOutExpo:function(t,e,n,i,r){return 0==e?n:e==r?n+i:1>(e/=r/2)?i/2*Math.pow(2,10*(e-1))+n:i/2*(-Math.pow(2,-10*--e)+2)+n},easeInCirc:function(t,e,n,i,r){return-i*(Math.sqrt(1-(e/=r)*e)-1)+n},easeOutCirc:function(t,e,n,i,r){return i*Math.sqrt(1-(e=e/r-1)*e)+n},easeInOutCirc:function(t,e,n,i,r){return 1>(e/=r/2)?-i/2*(Math.sqrt(1-e*e)-1)+n:i/2*(Math.sqrt(1-(e-=2)*e)+1)+n},easeInElastic:function(t,e,n,i,r){var o=1.70158,a=0,s=i;if(0==e)return n;if(1==(e/=r))return n+i;if(a||(a=.3*r),Math.abs(i)>s){s=i;var o=a/4}else var o=a/(2*Math.PI)*Math.asin(i/s);return-(s*Math.pow(2,10*(e-=1))*Math.sin((e*r-o)*2*Math.PI/a))+n},easeOutElastic:function(t,e,n,i,r){var o=1.70158,a=0,s=i;if(0==e)return n;if(1==(e/=r))return n+i;if(a||(a=.3*r),Math.abs(i)>s){s=i;var o=a/4}else var o=a/(2*Math.PI)*Math.asin(i/s);return s*Math.pow(2,-10*e)*Math.sin((e*r-o)*2*Math.PI/a)+i+n},easeInOutElastic:function(t,e,n,i,r){var o=1.70158,a=0,s=i;if(0==e)return n;if(2==(e/=r/2))return n+i;if(a||(a=r*.3*1.5),Math.abs(i)>s){s=i;var o=a/4}else var o=a/(2*Math.PI)*Math.asin(i/s);return 1>e?-.5*s*Math.pow(2,10*(e-=1))*Math.sin((e*r-o)*2*Math.PI/a)+n:.5*s*Math.pow(2,-10*(e-=1))*Math.sin((e*r-o)*2*Math.PI/a)+i+n},easeInBack:function(t,e,n,i,r,o){return void 0==o&&(o=1.70158),i*(e/=r)*e*((o+1)*e-o)+n},easeOutBack:function(t,e,n,i,r,o){return void 0==o&&(o=1.70158),i*((e=e/r-1)*e*((o+1)*e+o)+1)+n},easeInOutBack:function(t,e,n,i,r,o){return void 0==o&&(o=1.70158),1>(e/=r/2)?i/2*e*e*(((o*=1.525)+1)*e-o)+n:i/2*((e-=2)*e*(((o*=1.525)+1)*e+o)+2)+n},easeInBounce:function(e,n,i,r,o){return r-t.easing.easeOutBounce(e,o-n,0,r,o)+i},easeOutBounce:function(t,e,n,i,r){return 1/2.75>(e/=r)?i*7.5625*e*e+n:2/2.75>e?i*(7.5625*(e-=1.5/2.75)*e+.75)+n:2.5/2.75>e?i*(7.5625*(e-=2.25/2.75)*e+.9375)+n:i*(7.5625*(e-=2.625/2.75)*e+.984375)+n},easeInOutBounce:function(e,n,i,r,o){return o/2>n?.5*t.easing.easeInBounce(e,2*n,0,r,o)+i:.5*t.easing.easeOutBounce(e,2*n-o,0,r,o)+.5*r+i}})});