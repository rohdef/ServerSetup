# ServerSetup

## Prerequisites

- `sudo apt install sshpass`

## Running

Run with

```
$ ./gourmet.py -h
```

Set properties with flags using `-p`, e.g.:

```
$ ./gourmet.py \
    -p property1=value \
    -p "property2=slightly complex value" \
    -p "property with complex key=some value" \
    recipe.yaml
```

## Recipe format