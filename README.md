# Gourmet

Warning: This is a personal tool - feel free to use, etc. but there's no guarantees for your use case :)

Tool for automating install tasks for machines in a declarative manner.
Intended for servers, local machines etc., where tools like Teraform, helm and K8s Operators aren't useful.
Format is inspired by GitHub Actions to keep it easy to get started with

## Prerequisites for usage

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