//
//  DeclarationMainTypes.h
//  Ilmoitus
//
//  Created by Administrator on 23/05/14.
//  Copyright (c) 2014 42IN12EWa. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface DeclarationMainTypes : NSObject
// has error
// property with 'retain (or strong) attribute must be of object type'
@property (strong, nonatomic) NSInteger *MainTypeId;
@property (strong, nonatomic) NSString *MainTypeDescription;

@end
