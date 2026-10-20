"""Syntax-check modified production Kotlin and type-check the actual Android connectivity observer."""
from pathlib import Path
import os, subprocess, tempfile
root = Path(__file__).resolve().parents[2]
cache = Path(os.environ.get('GRADLE_USER_HOME', str(Path.home()/'.gradle')))
lib = next(cache.glob('wrapper/dists/gradle-8.11.1-bin/*/gradle-8.11.1/lib'))
cp = os.pathsep.join(str(p) for p in lib.glob('*.jar'))
java = Path(os.environ['JAVA_HOME'])/'bin'
files = [
 'app/src/main/java/app/veyra/music/ui/MainViewModel.kt',
 'app/src/main/java/app/veyra/music/MainActivity.kt',
 'app/src/main/java/app/veyra/music/data/home/HomeConnectivity.kt',
 'app/src/main/java/app/veyra/music/data/home/HomeDiskStore.kt',
 'shared/src/jvmSharedMain/kotlin/app/veyra/music/data/home/HomeLayout.kt',
 'sharedUi/src/jvmSharedMain/kotlin/app/veyra/music/ui/screens/HomeExperience.kt',
 'sharedUi/src/jvmSharedMain/kotlin/app/veyra/music/ui/screens/HomeScreen.kt',
]
with tempfile.TemporaryDirectory(prefix='veyra-home016-syntax-') as out:
    subprocess.run([str(java/'javac'),'-cp',cp,'-d',out,str(Path(__file__).with_name('VerifyKotlin.java'))],check=True)
    subprocess.run([str(java/'java'),'-cp',out+os.pathsep+cp,'VerifyKotlin',*[str(root/p) for p in files]],check=True)
    sdk=Path(os.environ.get('ANDROID_HOME','/workspace/android-sdk'))
    subprocess.run([str(java/'java'),'-cp',cp,'org.jetbrains.kotlin.cli.jvm.K2JVMCompiler',
        '-no-stdlib','-no-reflect','-classpath',cp+os.pathsep+str(sdk/'platforms/android-36/android.jar'),
        '-d',out,str(root/files[2])],check=True)
    print('HomeConnectivity.kt compiled against the actual Android API 36 jar; runtime connectivity not tested.')
