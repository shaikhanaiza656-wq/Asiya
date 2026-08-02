# System HUD (Solo Leveling style window)

Yeh ek real Android Studio/Gradle project hai — koi fake/dummy code nahi hai.
Har feature (clock, weather, tasks, file picker) asli Android API se kaam karta hai.

## Kya hai ismein
- `HudFrameView.kt` — Canvas se procedurally banaya gaya glowing neon frame (image jaisa)
- Home screen: real-time clock, date, weather widget (aapki OpenWeatherMap API key se)
- Tasks — real save/load (SharedPreferences)
- Files — real Android system file picker
- Settings — API key save karne ke liye
- Tools — abhi khali hai, aap bata do kya add karna hai

## Termux se GitHub par push karna

```bash
pkg install git -y
cd SystemHUD
git init
git add .
git commit -m "Initial HUD app"
git branch -M main
git remote add origin https://github.com/<aapka-username>/<repo-name>.git
git push -u origin main
```

Push hote hi `.github/workflows/build-apk.yml` GitHub Actions par apne aap chalega
aur APK bana dega.

## APK kaise milega
1. GitHub repo kholo → **Actions** tab
2. Jo bhi latest run chal raha hai / complete ho chuka hai usme click karo
3. Neeche **Artifacts** section mein `app-debug-apk` milega — usse download karo
4. Andar `app-debug.apk` hoga, wahi apne phone me install karo

## Weather widget on karne ke liye
1. https://openweathermap.org/api par free account bana kar API key lo
2. App kholo → Settings → key paste karo → Save
3. Location permission do — home screen par real weather dikhega

## Aage kya add hoga
Battery, aur jo bhi "Tools" chahiye — bata dena, wahi turant real functionality
ke saath add kar denge (koi fake data nahi).
