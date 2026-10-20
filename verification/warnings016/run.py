"""Compile selected real sources/API contracts with -Werror; run DSP and artwork JUnit tests.
Requires the bootstrapped Gradle Kotlin 2.3.20 cache and Android API 37 SDK.
"""
from pathlib import Path
import subprocess,os,tempfile
import argparse, hashlib, io, json, shutil, urllib.request, zipfile
parser=argparse.ArgumentParser(description="Targeted warning-regression checks; not a full Android build")
parser.add_argument('--api-jars',type=Path,help='Directory of extracted pinned classes jars; otherwise download verified jars into a temporary directory')
args=parser.parse_args()
root=Path(__file__).resolve().parents[2]
gradle=Path(os.environ.get('GRADLE_USER_HOME',str(Path.home()/'.gradle')))
cache=gradle/'caches/modules-2/files-2.1'
api_tmp=tempfile.TemporaryDirectory(prefix='veyra-warning-apis-')
jars=args.api_jars or Path(api_tmp.name)
for item in json.loads(Path(__file__).with_name('api-artifacts.json').read_text()):
    target=jars/(item['name']+'.jar')
    if args.api_jars:
        data=target.read_bytes()
    else:
        with urllib.request.urlopen(item['url'],timeout=60) as response:
            with zipfile.ZipFile(io.BytesIO(response.read())) as archive:
                data=archive.read('classes.jar')
        target.write_bytes(data)
    if hashlib.sha256(data).hexdigest()!=item['classesSha256']:
        raise SystemExit('API artifact checksum mismatch: '+item['name'])
java=str(Path(os.environ['JAVA_HOME'])/'bin/java') if os.environ.get('JAVA_HOME') else shutil.which('java')
libs=next(gradle.glob('wrapper/dists/gradle-8.11.1-bin/*/gradle-8.11.1/lib'))
compiler=next(cache.glob('org.jetbrains.kotlin/kotlin-compiler-embeddable/2.3.20/*/*.jar'))
std=next(cache.glob('org.jetbrains.kotlin/kotlin-stdlib/2.3.20/*/*.jar'))
runtime=[std,*jars.glob('*.jar'),Path(os.environ['ANDROID_HOME'])/'platforms/android-37.0/android.jar',*cache.glob('com.google.guava/guava/*/*/*.jar'),*cache.glob('androidx.annotation/annotation-jvm/*/*/*.jar'),*cache.glob('org.jetbrains/annotations/*/*/*.jar'),next(cache.glob('org.jetbrains.kotlinx/kotlinx-coroutines-core-jvm/1.9.0/*/*.jar')),*libs.glob('junit-*.jar'),*libs.glob('hamcrest-*.jar')]
cp=os.pathsep.join(map(str,runtime))
compiler_cp=os.pathsep.join(map(str,[compiler,std,next(cache.glob('org.jetbrains.kotlin/kotlin-script-runtime/2.3.20/*/*.jar')),next(cache.glob('org.jetbrains.kotlin/kotlin-reflect/1.6.10/*/*.jar')),next(cache.glob('org.jetbrains.kotlinx/kotlinx-coroutines-core-jvm/1.8.0/*/*.jar')),*libs.glob('annotations-*.jar')]))
files=[
'app/src/main/java/app/veyra/music/playback/EqualizerProcessor.kt',
'app/src/main/java/app/veyra/music/playback/SpatialAudioProcessor.kt',
'app/src/main/java/app/veyra/music/playback/TransitionFilterProcessor.kt',
'app/src/main/java/app/veyra/music/playback/audio/AudioBlock.kt',
'app/src/main/java/app/veyra/music/playback/audio/FloatAudioProcessor.kt',
'app/src/main/java/app/veyra/music/playback/audio/PcmBoundary.kt',
'app/src/main/java/app/veyra/music/playback/audio/PcmEncoding.kt',
'shared/src/commonMain/kotlin/app/veyra/music/playback/EqualizerCurve.kt',
'shared/src/commonMain/kotlin/app/veyra/music/playback/EqualizerPreset.kt',
'shared/src/commonMain/kotlin/app/veyra/music/playback/PeakLimiter.kt',
'app/src/main/java/app/veyra/music/data/spotify/LocalPlaylistStore.kt',
'shared/src/commonMain/kotlin/app/veyra/music/data/model/Models.kt',
'app/src/main/java/app/veyra/music/data/remote/EmbeddedArt.kt',
'app/src/test/java/app/veyra/music/EqualizerProcessorTest.kt',
'app/src/test/java/app/veyra/music/EmbeddedArtTest.kt',
'app/src/test/java/app/veyra/music/AudioProcessorFlushTest.kt',
'verification/warnings016/ApiContracts.kt',
]
with tempfile.TemporaryDirectory(prefix='veyra016-api-') as out:
 command=[java,'-cp',compiler_cp,'org.jetbrains.kotlin.cli.jvm.K2JVMCompiler','-Werror','-no-stdlib','-no-reflect','-classpath',cp,'-d',out,*[str(root/f) for f in files]]
 subprocess.run(command,check=True)
 print('Actual DSP, playlist and artwork production sources compiled with Kotlin 2.3.20 and real Android/Media3 API jars.')
 subprocess.run([java,'-cp',out+os.pathsep+cp,'org.junit.runner.JUnitCore','app.veyra.music.EqualizerProcessorTest','app.veyra.music.EmbeddedArtTest','app.veyra.music.AudioProcessorFlushTest'],check=True)
