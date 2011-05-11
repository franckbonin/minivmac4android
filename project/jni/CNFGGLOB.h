/*
	Configuration options used by both platform specific
	and platform independent code.

	This file is automatically generated by the build system,
	which tries to know what options are valid in what
	combinations. Avoid changing this file manually unless
	you know what you're doing.
*/

/* adapt to current compiler/host processor */

#define BigEndianUnaligned 0
#define LittleEndianUnaligned 0
#define MayInline inline
#define MayNotInline __attribute__((noinline))
#define SmallGlobals 0
#define cIncludeUnused 0

/* capabilities provided by platform specific code */

#define MySoundEnabled 1

#define MySoundRecenterSilence 0
#define kLn2SoundSampSz 3

#define DetailedAbnormalReport 0
#define ExtraAbnormalReports 0
#define MakeDumpFile 0

#define NumDrives 8
#define IncludeSonyRawMode 0
#define IncludeSonyGetName 0
#define IncludeSonyNew 0
#define IncludeSonyNameNew 0

#define vMacScreenHeight 342
#define vMacScreenWidth 512
#define vMacScreenDepth 0

#define kTrueROM_Size 0x020000
#define kROM_Size kTrueROM_Size

#define IncludePbufs 0

#define EnableMouseMotion 1

#define IncludeHostTextClipExchange 0
#define WantInitSpeedValue 3