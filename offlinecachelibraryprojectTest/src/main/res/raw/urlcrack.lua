
--[[---------------
LuaBit v0.4
-------------------
a bitwise operation lib for lua.

http://luaforge.net/projects/bit/

How to use:
-------------------
 bit.bnot(n) -- bitwise not (~n)
 bit.band(m, n) -- bitwise and (m & n)
 bit.bor(m, n) -- bitwise or (m | n)
 bit.bxor(m, n) -- bitwise xor (m ^ n)
 bit.brshift(n, bits) -- right shift (n >> bits)
 bit.blshift(n, bits) -- left shift (n << bits)
 bit.blogic_rshift(n, bits) -- logic right shift(zero fill >>>)
 
Please note that bit.brshift and bit.blshift only support number within
32 bits.

2 utility functions are provided too:
 bit.tobits(n) -- convert n into a bit table(which is a 1/0 sequence)
               -- high bits first
 bit.tonumb(bit_tbl) -- convert a bit table into a number 
-------------------

Under the MIT license.

copyright(c) 2006~2007 hanzhao (abrash_han@hotmail.com)
--]]---------------

do

------------------------
-- bit lib implementions

local function check_int(n)
 -- checking not float
 if(n - math.floor(n) > 0) then
  error("trying to use bitwise operation on non-integer!")
 end
end

local function to_bits(n)
 check_int(n)
 if(n < 0) then
  -- negative
  return to_bits(bit.bnot(math.abs(n)) + 1)
 end
 -- to bits table
 local tbl = {}
 local cnt = 1
 while (n > 0) do
  local last = math.mod(n,2)
  if(last == 1) then
   tbl[cnt] = 1
  else
   tbl[cnt] = 0
  end
  n = (n-last)/2
  cnt = cnt + 1
 end

 return tbl
end

local function tbl_to_number(tbl)
 local n = table.getn(tbl)

 local rslt = 0
 local power = 1
 for i = 1, n do
  rslt = rslt + tbl[i]*power
  power = power*2
 end
 
 return rslt
end

local function expand(tbl_m, tbl_n)
 local big = {}
 local small = {}
 if(table.getn(tbl_m) > table.getn(tbl_n)) then
  big = tbl_m
  small = tbl_n
 else
  big = tbl_n
  small = tbl_m
 end
 -- expand small
 for i = table.getn(small) + 1, table.getn(big) do
  small[i] = 0
 end

end

local function bit_or(m, n)
 local tbl_m = to_bits(m)
 local tbl_n = to_bits(n)
 expand(tbl_m, tbl_n)

 local tbl = {}
 local rslt = math.max(table.getn(tbl_m), table.getn(tbl_n))
 for i = 1, rslt do
  if(tbl_m[i]== 0 and tbl_n[i] == 0) then
   tbl[i] = 0
  else
   tbl[i] = 1
  end
 end
 
 return tbl_to_number(tbl)
end

local function bit_and(m, n)
 local tbl_m = to_bits(m)
 local tbl_n = to_bits(n)
 expand(tbl_m, tbl_n) 

 local tbl = {}
 local rslt = math.max(table.getn(tbl_m), table.getn(tbl_n))
 for i = 1, rslt do
  if(tbl_m[i]== 0 or tbl_n[i] == 0) then
   tbl[i] = 0
  else
   tbl[i] = 1
  end
 end

 return tbl_to_number(tbl)
end

local function bit_not(n)
 
 local tbl = to_bits(n)
 local size = math.max(table.getn(tbl), 32)
 for i = 1, size do
  if(tbl[i] == 1) then 
   tbl[i] = 0
  else
   tbl[i] = 1
  end
 end
 return tbl_to_number(tbl)
end

local function bit_xor(m, n)
 local tbl_m = to_bits(m)
 local tbl_n = to_bits(n)
 expand(tbl_m, tbl_n) 

 local tbl = {}
 local rslt = math.max(table.getn(tbl_m), table.getn(tbl_n))
 for i = 1, rslt do
  if(tbl_m[i] ~= tbl_n[i]) then
   tbl[i] = 1
  else
   tbl[i] = 0
  end
 end
 
 --table.foreach(tbl, print)

 return tbl_to_number(tbl)
end

local function bit_rshift(n, bits)
 check_int(n)
 
 local high_bit = 0
 if(n < 0) then
  -- negative
  n = bit_not(math.abs(n)) + 1
  high_bit = 2147483648 -- 0x80000000
 end

 for i=1, bits do
  n = n/2
  n = bit_or(math.floor(n), high_bit)
 end
 return math.floor(n)
end

-- logic rightshift assures zero filling shift
local function bit_logic_rshift(n, bits)
 check_int(n)
 if(n < 0) then
  -- negative
  n = bit_not(math.abs(n)) + 1
 end
 for i=1, bits do
  n = n/2
 end
 return math.floor(n)
end

local function bit_lshift(n, bits)
 check_int(n)
 
 if(n < 0) then
  -- negative
  n = bit_not(math.abs(n)) + 1
 end

 for i=1, bits do
  n = n*2
 end
 return bit_and(n, 4294967295) -- 0xFFFFFFFF
end

local function bit_xor2(m, n)
 local rhs = bit_or(bit_not(m), bit_not(n))
 local lhs = bit_or(m, n)
 local rslt = bit_and(lhs, rhs)
 return rslt
end

--------------------
-- bit lib interface

bit = {
 -- bit operations
 bnot = bit_not,
 band = bit_and,
 bor  = bit_or,
 bxor = bit_xor,
 brshift = bit_rshift,
 blshift = bit_lshift,
 bxor2 = bit_xor2,
 blogic_rshift = bit_logic_rshift,

 -- utility func
 tobits = to_bits,
 tonumb = tbl_to_number,
}

end

--[[
for i = 1, 100 do
 for j = 1, 100 do
  if(bit.bxor(i, j) ~= bit.bxor2(i, j)) then
   error("bit.xor failed.")
  end
 end
end
--]]






crack = {
implements='org.keplerproject.luajava.test.CrackInterface',

init = function(it)
	crack.it=it
	return "fds"

end ,

crackUrl = function(cracktype, number, url, callback, luaUtil)
	crack.callback = callback
	crack.luaUtil = luaUtil
	crack.originalUrl = url
	crack.cracksucceed = "cracksucceed"
	Log:e("crackUrl", crack.originalUrl)

	webFrom = crack["getName"](url)
	if webFrom == "sohu" then
	--[[	url = crack["sohuPreprocess"](url)]]
	url = string.gsub("http://vpsky.flvcd.com/yotian_m3u8.php?url=$url","%$url", url)
	elseif webFrom =="pptv" then
	--[[
		url = string.gsub(url,"v.pptv.com","m.pptv.com")	]]
		url = string.gsub("http://vpsky.flvcd.com/yotian_m3u8.php?url=$url&format=1","%$url", url)
	elseif webFrom =="qq" then

		url = string.gsub("http://vpsky.flvcd.com/yotian_m3u8.php?url=$url","%$url", url)
	elseif webFrom =="iqiyi" then
		url = string.gsub("http://vpsky.flvcd.com/yotian_m3u8.php?url=$url","%$url", url)
	elseif webFrom == "youku" then
		url = string.gsub("http://vpsky.flvcd.com/yotian_m3u8.php?url=$url","%$url", url)
	elseif webFrom == "letv" then
		url = string.gsub("http://vpsky.flvcd.com/yotian_m3u8.php?url=$url","%$url", url)
	end
	luaUtil:luaGetSourceCode(cracktype, crack.callback, number, url, crack.it, webFrom, "1", "")	
	
--[[	if(webFrom ~="qq") then
		
	elseif qqvid=="1" then
	Log:e("qqq", "fdaaa")
		luaUtil:luaGetSourceCode(cracktype, crack.callback, number, url, crack.it, webFrom, "1", "")
	else
		Log:e("qqq", qqvid)
		result = string.gsub("http://vv.video.qq.com/geturl?vid=$vid&otype=json&format=2&charge=0&appver=&platform=5&uin=&market_id=56", "%$vid", qqvid)
		Log:e("qqq", result)
		crack.luaUtil:luaGetSourceCode(cracktype, crack.callback, number, result, crack.it, "qq", "2", asistInfo)
	end
	Log:e("crackUrl", "luaGetSourceCode")
	]]
end ,

getName = function(url)
	Log:e("getName", "getName")
	if string.find(url, "cntv") ~= nil then
		return "cntv"
	
	else
		Log:e("getName", "else")
		result = string.sub(url, string.find(url, "%w+.com"))
		result = string.sub(result, string.find(result, "%w+"))
		return result
	end
end ,

checkqq = function(url)
	Log:e("checkqq", "checkqq")
	if string.find(url, "vid") == nil then
		return "1"
	else
		result = string.sub(url, string.find(url, "vid=%w+"))
		Log:e("checkqq", "checkqq"..result)
		result = string.sub(result, 5,-1)
		Log:e("checkqq", "checkqq"..result)
		return result
	end
end ,





dispatch = function(cracktype, number, sourceCode, toFunc, phase, asistInfo)
	Log:e("dispatch", toFunc..phase)
	crack[toFunc](cracktype, number, sourceCode, phase, asistInfo)
end ,

callTheCallback = function(result, number)
	if (crack.luaUtil:canPlay(number)) then
		crack.callback:onComplete(result, number)
	else
		crack.callback:onComplete(nil, number)
	end
end ,

youku = function(cracktype, number, url, phase, asistInfo)
	_,_,result = string.find(url, "U\":.-(http.-)\"")
	luaResult = luajava.newInstance("java.util.HashMap")
	luaResult:put("standardDef", result)
	Log:i("youku result", "result")
	crack.callback:onComplete(luaResult, number, crack.originalUrl, crack.cracksucceed)
	
	
	--[[Log:e("youku", "youku")
	if phase == "1" then
		vedioId2String = string.sub(url, string.find(url, "var%svideoId2=%s'%w+'"))
		vedioId2 = string.sub(vedioId2String, string.find(vedioId2String, "'%w+'"))
		vedioId = string.sub(vedioId2, string.find(vedioId2, "%w+"))
		result = string.gsub("http://api.flvxz.com/site/youku/vid/$id/ftype/m3u8/jsonp/purejson", "%$id", vedioId)
		crack.luaUtil:luaGetSourceCode(cracktype, crack.callback, number, result, crack.it, "youku", "2", vedioId)
	elseif phase == "2" then
		Log:e("aa",url)
		temp1 = luajava.newInstance("org.json.JSONArray",string.sub(url,1,-1))
		Log:e("aaa",temp1)
		for i = 0,temp1:length()-1 do
				Log:e("aa","aaaa")
				end
				
		end
		
		Log:e("aa","55555")
		
		jsonobject = luajava.newInstance("org.json.JSONObject",tostring(url))
		Log:e("aa","222444232321")
		temp1 = luajava.newInstance("org.json.JSONArray")
		Log:e("aa","222433444")
		temp1 = jsonobject:getJSONArray(0)
	
		Log:e("aa","22244444")
		jsonarray1 = luajava.newInstance("org.json.JSONArray")
		Log:e("aa","121212")
		jsonobject1 = luajava.newInstance("org.json.JSONObject")
		Log:e("aa","2333333")
		for i = 0,temp1:length()-1 do
				jsonobject = temp1:getJSONObject(i)
				Log:e("aa","111")
				jsonarray1 = jsonobject:getJSONArray("files")
				jsonobject1 = jsonarray1:getJSONObject(0)
				Log:e("aa","222")
				result = jsonobject1:getString("furl")
				Log:e("aa",result)
				if string.find(result,"mp4") then
					luaResult:put("hightDef", result)
				elseif string.find(result,"hd2") then
					luaResult:put("superDef", result)
				elseif string.find(result,"flv") then
					luaResult:put("standardDef", result)
				end
				
		end
		--og:i("number", number)
		--Log:i("crack.originalUrl", crack.originalUrl)
		--Log:i(" crack.cracksucceed",  crack.cracksucceed)]]
		--crack.callback:onComplete(luaResult, number, crack.originalUrl, crack.cracksucceed)
		--Log:i("youku", "crack.callback:onComplete")
		--[[
		luaResult = luajava.newInstance("java.util.HashMap")
		Log:i("url", url)
		if string.find(url, "furl\":.-,") then
		Log:i("url1", url)
			result = string.sub(url, string.find(url, "furl\":.-,"))
			_,_,result = string.find(result, "(http:.-)\"")
			result = string.gsub(result, "\\", "")
			luaResult:put("standardDef", result)
			Log:i("result", result)
			Log:i("youku", "crack.callback:onComplete1")
		end
		]]
		--[[if string.find(url, "furl\":.-mp4.-,") then
			result1 = string.sub(url, string.find(url, "furl\":.-mp4.-,"))
			_,_,result1 = string.find(result1, "(http:.-)\"")
			result1 = string.gsub(result1, "\\", "")
			luaResult:put("hightDef", result1)
			Log:i("youku", "crack.callback:onComplete2")
		end
		if string.find(url, "furl\":.-hd2.-,") then
			result2 = string.sub(url, string.find(url, "furl\":.-hd2.-,"))
			_,_,result2 = string.find(result2, "(http:.-)\"")
			result2 = string.gsub(result2, "\\", "")
			luaResult:put("superDef", result2)
			Log:i("youku", "crack.callback:onComplete3")
		end]]
		--[[
		crack.callback:onComplete(luaResult, number, crack.originalUrl, crack.cracksucceed)
		Log:i("youku", "crack.callback:onComplete")
		]]
	--[[
		list = string.sub(url, string.find(url, "streamtypes.-],"))
		luaResult = luajava.newInstance("java.util.HashMap")
		if string.find(list, "flv") then
			result = string.gsub("http://v.youku.com/player/getM3U8/vid/$id/type/flv/v.m3u8", "%$id", asistInfo)
			--result = string.gsub("http://pl.youku.com/playlist/m3u8?ts=$time&keyframe=1&vid=$id&type=flv", "%$id", asistInfo)
			--result = string.gsub(result, "%$time", os.time())
			luaResult:put("standardDef", result)
			Log:i("youku", "crack.callback:onComplete1")
		end
		if string.find(list, "mp4") then
			result = string.gsub("http://v.youku.com/player/getM3U8/vid/$id/type/mp4/v.m3u8", "%$id", asistInfo)
			--result = string.gsub("http://pl.youku.com/playlist/m3u8?ts=$time&keyframe=1&vid=$id&type=mp4", "%$id", asistInfo)
			--result = string.gsub(result, "%$time", os.time())
			luaResult:put("hightDef", result)
			Log:i("youku", "crack.callback:onComplete2")
		end
		if string.find(list, "hd2") then
			result = string.gsub("http://v.youku.com/player/getM3U8/vid/$id/type/hd2/v.m3u8", "%$id", asistInfo)
			--result = string.gsub("http://pl.youku.com/playlist/m3u8?ts=$time&keyframe=1&vid=$id&type=hd2", "%$id", asistInfo)
			--result = string.gsub(result, "%$time", os.time())
			luaResult:put("superDef", result)
			Log:i("youku", "crack.callback:onComplete3")
		end
		crack.callback:onComplete(luaResult, number, crack.originalUrl, crack.cracksucceed)
		Log:i("youku", "crack.callback:onComplete")
		]]
		--[[
	end
	]]
end ,

tudou = function(cracktype, number, url, phase, asistInfo)
	Log:e("tudou", "tudou")
	vcode = string.sub(url, string.find(url, "vcode:.-,"))
	vcode = string.sub(vcode, string.find(vcode, ":.-,"))
	if (string.find(vcode, "%w+")) then
		vcode = string.sub(vcode, string.find(vcode, "%w+"))
		result = string.gsub("http://v.youku.com/player/getM3U8/vid/$id/type/mp4/v.m3u8", "%$id", vcode)
		luaResult = luajava.newInstance("java.util.HashMap")
		luaResult:put("standardDef", result)
		crack.callback:onComplete(luaResult, number, crack.originalUrl, crack.cracksucceed)
	else
		iidString = string.sub(url, string.find(url, "iid.-:.-%d+"))
		iid = string.sub(iidString, string.find(iidString, "%d+"))
		result = string.gsub("http://vr.tudou.com/v2proxy/v2.m3u8?it=$iid&st=2", "%$iid", iid)
		luaResult = luajava.newInstance("java.util.HashMap")
		luaResult:put("standardDef", result)
		crack.callback:onComplete(luaResult, number, crack.originalUrl, crack.cracksucceed)
	end
end ,

sohuPreprocess = function(url)
	if string.find(url, "//tv.sohu") then
		url = string.gsub(url, "//tv.sohu", "//m.tv.sohu")
		return url
	elseif string.find(url, "my.tv.sohu") then
		url = string.gsub(url, "my.tv.sohu", "m.tv.sohu")
		return url
	elseif string.find(url, "s.sohu.com") then
		url = string.gsub(url, "s.sohu.com", "m.s.sohu.com")
		return url
	else
		return url
	end
end ,

sohu = function(cracktype, number, url, phase, asistInfo)
	_,_,result = string.find(url, "U\":.-(http.-)\"")
	luaResult = luajava.newInstance("java.util.HashMap")
	luaResult:put("standardDef", result)
	Log:i("sohu result", result)
	crack.callback:onComplete(luaResult, number, crack.originalUrl, crack.cracksucceed)
--[[if phase == "1" then
	Log:e("mysohu", "mysohu")
	s = string.sub(url, string.find(url, "vid%s\:%s\"%w+\""))
	vid = string.sub(s,string.find(s,"\".-\""))
	Log:e("mysohu1111", vid)
	vid = string.sub(vid,2,-2)
	Log:e("mysohu1111", vid)
	result = string.gsub("http://api.tv.sohu.com/v4/video/info/$vid.json?site=1&callback=initLoadVideoCallback&api_key=695fe827ffeb7d74260a813025970bd5&plat=3&sver=1.0&partner=1&n=10","$vid",vid)
	Log:e("mysohu1111", result)
	crack.luaUtil:luaGetSourceCode(cracktype, crack.callback, number, result, crack.it, "sohu", "2", vedioId)
	elseif phase == "2" then
		regs = "[^\\{]*?(\\{.*\\})"
		Log:e("mysohu1111", url)
		pattern = luajava.bindClass("java.util.regex.Pattern")
		matcher = luajava.bindClass("java.util.regex.Matcher")
		matcher = pattern:compile(regs):matcher(url);
		if matcher:find() then
		
			jsonobject = luajava.newInstance("org.json.JSONObject",matcher:group(1))
			jsonarray = luajava.newInstance("org.json.JSONArray")
			s = luajava.newInstance("org.json.JSONObject")
			s = jsonobject:getJSONObject("data")
			luaResult = luajava.newInstance("java.util.HashMap")
		
			result1 = s:optString("url_nor")
			if(string.len(result1)>0) then
				luaResult:put("standardDef", result1)
			end
				result1 = s:optString("url_high")
			if(string.len(result1)>0) then
				Log:e("result2", result1)
				luaResult:put("hightDef", result1)
			end
			result1 = s:optString("url_super")
			if(string.len(result1)>0) then
				Log:e("result2", result1)
				luaResult:put("superDef", result1)
			end
			crack.callback:onComplete(luaResult, number, crack.originalUrl, crack.cracksucceed)
		end
			

	end
	]]
end ,



pptv =	function(cracktype, number, url, phase, asistInfo)
	if phase == "1" then
	_,_,result = string.find(url, "C\":.-(http.-)\"")
	_,_,result1 = string.find(url, "U\":.-(http.-)\"")
	luaResult = luajava.newInstance("java.util.HashMap")

	Log:i("pptv result", result)
	
	
	crack.luaUtil:luaGetSourceCode(cracktype, crack.callback, number, result, crack.it, "pptv", "2", asistInfo)
	
	elseif phase == "2" then

	ip = string.sub(url,string.find(url,"<sh>.-</sh>"))
	Log:i("pptv ip result", ip)
	ip = string.sub(ip,5,-6)
	Log:i("pptv ip result", ip)
	result = string.gsub(result1,"v.pptv.com",ip)
	Log:i("pptv result", result)
	luaResult:put("standardDef", result)
	crack.callback:onComplete(luaResult, number, crack.originalUrl, crack.cracksucceed)
	end
	
	
	
--[[	if phase == "1" then
		channelid = string.sub(url,string.find(url,"channel_id\":%w+,\""))
		channelid = string.sub(channelid,13,-3)
		if(string.find(url,"segment\":\".-\"")) then
			segment	= string.sub(url,string.find(url,"segment\":\".-\""))
			segment = string.sub(segment,11,-2)
			Log:e("pptv","pptv"..segment)
		end
		
		kk	= string.sub(url,string.find(url,"kk\":\".-\""))
		kk = string.sub(kk,6,-2)
		Log:e("pptv","pptv"..kk)
		
		result = string.format("http://web-play.pptv.com/webplay3-0-%s.xml?version=4&type=mpptv&kk=%s&o=m.pptv.com&cb=getPlayEncode", channelid, kk)
		Log:e("pptv","pptv"..result)
		crack.luaUtil:luaGetSourceCode(cracktype, crack.callback, number, result, crack.it, "pptv", "2", asistInfo)
	elseif phase =="2" then
		Log:e("pptv","pptv2"..1111111111111)
		pattern = luajava.bindClass("java.util.regex.Pattern")
		matcher = luajava.bindClass("java.util.regex.Matcher")
		Log:e("pptv","pptv2"..33333333)
		regs = "[^\\{]*?(\\{.*\\})"
		Log:e("pptv","pptv2"..22222222222)
		im = 0;
		matcher = pattern:compile(regs):matcher(url);
			Log:e("pptv","pptv2"..4444444444)
		if matcher:find() then
			Log:e("pptv","pptv2"..555555)

			jsonobject = luajava.newInstance("org.json.JSONObject",matcher:group(1))
			jsonarray = luajava.newInstance("org.json.JSONArray")
			Log:e("pptv","pptv2"..6666)
			s = jsonobject:optInt("sub")
			T={}
			T1={}
			jsonarray = jsonobject:getJSONArray("childNodes")
			for i = 0,jsonarray:length()-1 do
				jsonobject1 = luajava.newInstance("org.json.JSONObject")
				jsonobject1 = jsonarray:getJSONObject(i)
		--		ss = jsonobject1:optString("tagName")
				tagName = jsonobject1:optString("tagName")
				if tagName == "channel" then
					jsonarray1 = luajava.newInstance("org.json.JSONArray")
					jsonarray1 = jsonobject1:getJSONArray("childNodes")
					Log:e("pptv","pptv2"..777)
					for m = 0,jsonarray1:length()-1 do
						jsonobject2 = luajava.newInstance("org.json.JSONObject")
						jsonobject2 = jsonarray1:getJSONObject(m)
						tagName1 = jsonobject2:optString("tagName")
						Log:e("pptv","pptv2"..8888)
						if tagName1 == "file" then
							jsonarray2 = luajava.newInstance("org.json.JSONArray")
							jsonarray2 = jsonobject2:getJSONArray("childNodes")
Log:e("pptv","pptv2"..9999)
							for n = 0,jsonarray2:length()-1 do
								jsonobject3 = luajava.newInstance("org.json.JSONObject")
								jsonobject3 = jsonarray2:getJSONObject(n)
								temprid = jsonobject3:optString("rid")
								Log:e("aaccc",temprid.."rid")
								temprid = string.sub(temprid,1,-5)
								Log:e("aaccc",temprid.."rid")
								tempft = jsonobject3:optInt("ft")
								Log:e("aaccc",tempft.."ft")
					--			T=["ft"]=tempft,["rid"] = temprid})
								T[tempft]=temprid
							end
						end
					end

					elseif tagName == "dt" then
						jsonarray3 = luajava.newInstance("org.json.JSONArray")
						jsonarray3 = jsonobject1:getJSONArray("childNodes")
						Log:e("pptv","pptv2"..777888)
						tempft = jsonobject1:optInt("ft")
						Log:e("pptv","tempft"..tempft)
					for m = 0,jsonarray3:length()-1 do
						jsonobject4 = luajava.newInstance("org.json.JSONObject")
						jsonobject4 = jsonarray3:getJSONObject(m)
						tagname2 = jsonobject4:optString("tagName")
						if tagname2 =="sh" then
							Log:e("pptv","pptv2"..777999)
							jsonarray4 = luajava.newInstance("org.json.JSONArray")
							jsonarray4 = jsonobject4:getJSONArray("childNodes")
							tempsh = jsonarray4:getString(0)
							Log:e("pptv","pptv2"..temprid)

						elseif tagname2 == "key" then
							Log:e("pptv","pptv2"..77766)
							jsonarray5 = luajava.newInstance("org.json.JSONArray")
							jsonarray5 = jsonobject4:getJSONArray("childNodes")
							tempkey = jsonarray5:getString(0)
							Log:e("pptv","pptv2"..tempkey)

						end
					--	table.insert(T1,{["ft"]=m,["sh"]=tempsh,["key"]=tempkey})
						T1[tempft]={["sh"]=tempsh,["key"]=tempkey}
					end


				end
				
				]]
		--[[	for i=0, table.maxn(T)-1  do
				ssss=T[i]
				Log:e("result","ft"..ssss)
			end
]]

--[[
			end
			luaResult = luajava.newInstance("java.util.HashMap")
			for i=0, table.maxn(T) do
				
				if segment ~=nil then
					result = string.format("http://%s/%s.m3u8?type=mpptv&k=%s&segment=%s",T1[i]["sh"],T[i],T1[i]["key"],segment)
				else
					result = string.format("http://%s/%s.m3u8?type=mpptv&k=%s&segment=",T1[i]["sh"],T[i],T1[i]["key"])
				end
				if i ==0 then
				luaResult:put("standardDef", result)
				elseif i==1 then
				luaResult:put("hightDef", result)
				elseif i== 2 then
				luaResult:put("superDef", result)
				end

			end

			crack.callback:onComplete(luaResult, number, crack.originalUrl, crack.cracksucceed)

		end

	end
]]
end ,

hunantv=function(cracktype, number, url, phase, asistInfo)
	Log:e("aaa","url")
		if phase == "1" then
        _,_,code =string.find(url, "code:%s\"(%w+)\"")
        limit_rate = url:match("limit_rate: .-,")  --提取包含头尾的字符串
        limit_rate = limit_rate:sub(13, string.len(limit_rate)-1)
        file = url:match("file: .-,")  --提取包含头尾的字符串
        file =file:sub(8,string.len(file)-2)
        result = string.format("http://pcvcr.cdn.imgo.tv/ncrs/vod.do?fid=%s&limitrate=%d&file=%s&fmt=6&pno=3&m3u8=1",code,limit_rate*1.2,file)
		Log:e("huan phase1 result:",result)
		crack.luaUtil:luaGetSourceCode(cracktype, crack.callback, number, result, crack.it, "hunantv", "2", asistInfo)
	elseif phase == "2" then
        result = url:match("\"info\":.-,")
        _,_,result =string.find(result, "(http.-\")")
        result = string.sub(result, 1, -2)
		luaResult = luajava.newInstance("java.util.HashMap")
		luaResult:put("standardDef",result)
		crack.callback:onComplete(luaResult, number, crack.originalUrl, crack.cracksucceed)
    end

end,


qq = function(cracktype, number, url, phase, asistInfo)
	_,_,result = string.find(url, "U\":.-(http.-)\"")
	luaResult = luajava.newInstance("java.util.HashMap")
	luaResult:put("standardDef", result)
	Log:i("qq result", "result")
	crack.callback:onComplete(luaResult, number, crack.originalUrl, crack.cracksucceed)

	--[[if phase == "1" then
		Log:e("qq", "url"..url)
		vid = string.sub(url, string.find(url, "vid:\"%w+\""))
		Log:e("qq", "vid1"..vid)
		vid = string.sub(vid, string.find(vid, "\"%w+\""))
		Log:e("qq", "vid2"..vid)
		vid = string.sub(vid, string.find(vid, "%w+"))
		Log:e("qq", "vid3"..vid)
		result = string.gsub("http://vv.video.qq.com/geturl?vid=$vid&otype=json&format=2&charge=0&appver=&platform=5&uin=&market_id=56", "%$vid", vid)
		crack.luaUtil:luaGetSourceCode(cracktype, crack.callback, number, result, crack.it, "qq", "2", asistInfo)
	elseif phase == "2" then
	Log:e("qq", "url2"..url)
		result = string.sub(url, string.find(url, "url\".-\","))
		result = string.sub(result, string.find(result, "http.-\""))
		result = string.sub(result, 1, -2)
		luaResult = luajava.newInstance("java.util.HashMap")
		luaResult:put("standardDef", result)
		crack.callback:onComplete(luaResult, number, crack.originalUrl, crack.cracksucceed)
	else

	end]]
end ,

letv = function(cracktype, number, url, phase, asistInfo)
	_,_,result = string.find(url, "U\":.-(http.-)\"")
	luaResult = luajava.newInstance("java.util.HashMap")
	luaResult:put("standardDef", result)
	Log:i("letv result", result)
	crack.callback:onComplete(luaResult, number, crack.originalUrl, crack.cracksucceed)
--[[
	if phase == "1" then
		Log:e("letv", "phase1")
		
			vid = string.sub(url, string.find(url, "vid:%d+"))
			vid = string.sub(vid,5,-1)
			Log:e("letv333",vid)
			math.randomseed(os.time())
			result = string.gsub("http://api.letv.com/time?tn=$time", "%$time", math.random())
			Log:e("letv333",result)
		crack.luaUtil:luaGetSourceCode(cracktype, crack.callback, number, result, crack.it, "letv", "2", asistInfo)
	elseif phase == "2" then
	
		Log:e("letv", "phase2"..url)
		t = string.sub(url,string.find(url,"stime\":.-}"))
		t = string.sub(t, 8, -2)
		e = 0
		Log:e("letv", "aaa"..t)
		t = tonumber(t)
		for s=0,7,1 do
			e = bit.band(1,t)
			t = bit.brshift(t,1)
			e = bit.blshift(e,31)
			t = t+e
		end
		t = bit.bxor(t,185025305)
		Log:e("letv", "key"..t)
		result = string.format("http://api.letv.com/mms/out/video/play?id=%s&platid=3&splatid=301&domain=http://www.letv.com&format=1&tkey=%s", vid,t)
		Log:e("letv", "key"..result)
		crack.luaUtil:luaGetSourceCode(cracktype, crack.callback, number, result, crack.it, "letv", "3", asistInfo)
	elseif phase == "3" then
		Log:e("letv", "phase3"..url)
		result = string.sub(url, string.find(url, "dispatch\".-\","))
		num = string.sub(result,string.find(result,"dispatch\":{\".-\""))
		num = string.sub(num,13,-2)
		Log:e("letv", "phase3333"..num)
		video = string.sub(result,string.find(result,"%[.-,"))
		Log:e("letv", "phase3333"..video)
		video = string.sub(video,3,-3)
		Log:e("letv", "phase3333"..video)
		result = video.."&format=1&expect=2"
		Log:e("letv", "phase3333"..result)
		crack.luaUtil:luaGetSourceCode(cracktype, crack.callback, number, result, crack.it, "letv", "4", asistInfo)
	elseif phase == "4" then
		Log:e("letv", "phase4"..url)
		jsonobject = luajava.newInstance("org.json.JSONObject",url)
		s = jsonobject:getString("location")
		Log:e("letv", "phase444"..s)
		luaResult = luajava.newInstance("java.util.HashMap")
		]]
	--[[	result = string.sub(url,string.find(url,"location\": \".-\""))
		result = string.sub(result,13,-2)
		Log:e("letv", "phase444"..result)
		luaResult = luajava.newInstance("java.util.HashMap")]]
	--[[	luaResult:put("standardDef",s)
		crack.callback:onComplete(luaResult, number, crack.originalUrl, crack.cracksucceed)

	end
	]]
end ,

cntv = function(cracktype, number, url, phase, asistInfo)
	Log:e("cntv", "cntv")
	vid = string.sub(url, string.find(url, "videoCenterId.-;"))
	vid = string.sub(vid, string.find(vid, "\"%w+\""))
	vid = string.sub(vid, string.find(vid, "%w+"))
	result = string.gsub("http://asp.cntv.lxdns.com/hls/$videoCenterId/main.m3u8", "%$videoCenterId", vid)
	luaResult = luajava.newInstance("java.util.HashMap")
	luaResult:put("standardDef", result)
	crack.callback:onComplete(luaResult, number, crack.originalUrl, crack.cracksucceed)
	--crack["callTheCallback"](result, number)
end ,

iqiyi = function(cracktype, number, url, phase, asistInfo)

	_,_,result = string.find(url, "U\":.-(http.-)\"")
	luaResult = luajava.newInstance("java.util.HashMap")
	luaResult:put("standardDef", result)
	Log:i("iqiyi result", "result")
	crack.callback:onComplete(luaResult, number, crack.originalUrl, crack.cracksucceed)

	--[[
	pattern = luajava.bindClass("java.util.regex.Pattern")
	matcher = luajava.bindClass("java.util.regex.Matcher")
	if phase == "1" then
		_, _, vid = string.find(url, "data%-player%-tvid=\"(%w+)\"")
		_, _, vid1 = string.find(url, "data%-player%-videoid=\"(%w+)\"")			
		result = string.format("http://cache.m.iqiyi.com/jp/tmts/%s/%s/?uid=&cupid=qc_100001_100103&type=mp4", vid, vid1)
		Log:e("iqiyi phase1 result:",result)
		crack.luaUtil:luaGetSourceCode(cracktype, crack.callback, number, result, crack.it, "iqiyi", "2", asistInfo)
	elseif phase == "2" then						
		luaResult = luajava.newInstance("java.util.HashMap")
		_,_,res = string.find(url,"data\".-\"m3u\":.-\"(.-)\"")
		Log:e("iqiyi result:", res)
		luaResult:put("standardDef", res)
		crack.callback:onComplete(luaResult, number, crack.originalUrl, crack.cracksucceed)	
	else
	end
	]]
end
}








