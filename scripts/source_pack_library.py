"""Offline, lossless source-archive storage for immutable runtime ZIP packs."""
import hashlib,io,lzma,tarfile
from pathlib import PurePosixPath
MAX_PACK=12*1024*1024
MAX_LIBRARY=160*1024*1024
LIBRARY='SOURCE_DATA_PACKS.tar.xz'

def valid_path(name):
 p=PurePosixPath(name)
 return not p.is_absolute() and '..' not in p.parts and len(p.parts)==3 and p.parts[:2] in (('themes','packs'),('asset-packs','packs')) and p.suffix=='.zip'

def encode(packs):
 buffer=io.BytesIO()
 with tarfile.open(fileobj=buffer,mode='w') as tar:
  for path,data in sorted(packs.items()):
   if not valid_path(path) or not 0<len(data)<=MAX_PACK:raise ValueError('Invalid runtime pack')
   info=tarfile.TarInfo(path);info.size=len(data);info.mode=0o644;info.mtime=0
   tar.addfile(info,io.BytesIO(data))
 return lzma.compress(buffer.getvalue(),format=lzma.FORMAT_XZ,preset=6,filters=None)

def decode(data,expected):
 # Bound decompression, archive membership and every original ZIP byte hash.
 if len(expected)>200:raise ValueError('Too many source packs')
 reader=lzma.LZMADecompressor(memlimit=128*1024*1024)
 raw=reader.decompress(data,max_length=MAX_LIBRARY+1)
 if len(raw)>MAX_LIBRARY or not reader.eof or reader.unused_data:raise ValueError('Invalid source pack library size')
 wanted={e['path']:e for e in expected}
 if len(wanted)!=len(expected) or not all(valid_path(p) for p in wanted):raise ValueError('Invalid source pack index')
 result={}
 with tarfile.open(fileobj=io.BytesIO(raw),mode='r:') as tar:
  for entry in tar:
   if not entry.isfile() or entry.name not in wanted or entry.name in result or not 0<entry.size<=MAX_PACK:raise ValueError('Invalid source pack member')
   blob=tar.extractfile(entry).read(MAX_PACK+1);record=wanted[entry.name]
   if len(blob)!=record['bytes'] or hashlib.sha256(blob).hexdigest()!=record['sha256']:raise ValueError('Source pack checksum mismatch')
   result[entry.name]=blob
 if result.keys()!=wanted.keys():raise ValueError('Missing source packs')
 return result
