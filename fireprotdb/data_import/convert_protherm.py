import yaml

from fireprotdb.importer.protherm import ProThermConverter
from fireprotdb.importer.utils import cache_file


def _to_dict(pm):
    s = pm.__dict__
    s["alignment"] = pm.alignment.__dict__
    s["data"] = pm.data.__dict__
    s["experimental_condition"] = pm.experimental_condition.__dict__

    return s


def convert_protherm():
    importer = ProThermConverter()
    data = importer.run()

    with open(cache_file("protherm.yaml"), 'w') as fh:
        fh.write(yaml.safe_dump([_to_dict(pm) for pm in data]))


if __name__ == "__main__":
    convert_protherm()
