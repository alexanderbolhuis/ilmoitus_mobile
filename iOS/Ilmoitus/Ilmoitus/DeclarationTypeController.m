//
//  DeclarationTypeController.m
//  Ilmoitus
//
//  Created by Administrator on 23/05/14.
//  Copyright (c) 2014 42IN12EWa. All rights reserved.
//

#import "constants.h"
#import "DeclarationTypeController.h"
#import "DeclarationMainTypes.h"
#import "DeclarationSubTypes.h"

@interface DeclarationTypeController()

@property (strong, nonatomic) NSMutableArray *mainTypes;
@property (strong, nonatomic) NSMutableArray *subTypes;

@end

@implementation DeclarationTypeController

- (NSMutableArray*)downLoadMainTypes
{
    NSMutableArray *declarationsTypesFound = [[NSMutableArray alloc] init];
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    manager.requestSerializer = [AFHTTPRequestSerializer serializer];
    [manager.requestSerializer setValue:[[NSUserDefaults standardUserDefaults] stringForKey:@"token"] forHTTPHeaderField:@"Authorization"];
    NSString *url = [NSString stringWithFormat:@"%@/declarationtypes", baseURL];
    [manager GET:url parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject)
    {
        NSError* error;
        NSDictionary* json = [NSJSONSerialization
                              JSONObjectWithData:responseObject
                               
                              options:kNilOptions
                              error:&error];
         
        
        for (NSDictionary *decl in json)
        {
            DeclarationMainTypes *declarationMainTypes = [[DeclarationMainTypes alloc] init];
            *declarationMainTypes.mainTypeId = [decl[@"id"] longLongValue];
            declarationMainTypes.mainTypeDescription = [decl[@"declarationType"] stringValue];
            [declarationsTypesFound addObject:declarationMainTypes];
        }
        
        NSLog(@"GET request success response for all declarations: %@", json);
    }
         failure:^(AFHTTPRequestOperation *operation, NSError *error)
    {
        NSLog(@"GET request Error for all declarations main types: %@", error);
    }];
    return declarationsTypesFound;
}

// expect the id from the DeclarationMainType
- (NSMutableArray*)downLoadSubTypes:(NSInteger*)mainTpyeId
{
    NSString *combinedURL = [NSString stringWithFormat:@"%@%@", baseURL, @"/declarationsubtype/"];
    NSMutableArray *declarationsSubTypesFound = [[NSMutableArray alloc] init];
    
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    manager.requestSerializer = [AFHTTPRequestSerializer serializer];
    [manager.requestSerializer setValue:[[NSUserDefaults standardUserDefaults] stringForKey:@"token"] forHTTPHeaderField:@"Authorization"];
    NSString *url = [NSString stringWithFormat:[NSString stringWithFormat:@"%i", *mainTpyeId], combinedURL];
    [manager GET:url parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject)
    {
        NSError* error;
        NSDictionary* json = [NSJSONSerialization
                              JSONObjectWithData:responseObject
                              
                              options:kNilOptions
                              error:&error];
        
        
        for (NSDictionary *decl in json)
        {
            DeclarationSubTypes *declarationSubTypes = [[DeclarationSubTypes alloc] init];
            *declarationSubTypes.subTypeId = [decl[@"id"] longLongValue];
            declarationSubTypes.subTypeDescription = [decl[@"declarationType"] stringValue];
            *declarationSubTypes.subTypeMaxCost = [decl[@"maxCost"] decimalValue];
            [declarationsSubTypesFound addObject:declarationSubTypes];
        }
        
        NSLog(@"GET request success response for all declarations sub types: %@", json);
    }
         failure:^(AFHTTPRequestOperation *operation, NSError *error)
    {
        NSLog(@"GET request Error for all declarations: %@", error);
    }];
    return declarationsSubTypesFound;
}

@end
