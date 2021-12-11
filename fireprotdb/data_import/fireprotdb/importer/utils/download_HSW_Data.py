import re,os,glob, requests,zipfile

jobs = open("../../../cache/actPDB2.txt")

for line in jobs.readlines():
    print(line)
    line = line[0:-1]
    parts = re.split(" ", line)

    r = requests.get("https://loschmidt.chemi.muni.cz/hotspotwizard/download?file=archive&job=" + parts[2])
    open("../../../cache/hswJana/" + parts[0] + ".zip", "wb").write(r.content)
    with zipfile.ZipFile("../../../cache/hswJana/" + parts[0] + ".zip", "r") as zip_ref:
        zip_ref.extractall("../../../cache/hswJana/" + parts[0])
    os.remove("../../../cache/hswJana/" + parts[0] + ".zip")